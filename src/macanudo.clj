(ns macanudo
  (:import  [java.text SimpleDateFormat]
            [java.util Date]
            [java.net URL]
            [org.htmlcleaner HtmlCleaner PrettyXmlSerializer CleanerProperties])
  (:require [clojure.xml :as xml]
            [clojure.zip :as z]
            [clojure.pprint :as p]
            [clojure.java.io :as io]))

(defn xml-str [s]
  (xml/parse (java.io.ByteArrayInputStream. (.getBytes s "UTF-8"))))

(defn clean-html [url]
  (let [cleaner    (HtmlCleaner.)
        node       (.clean cleaner (URL. url))
        serializer (PrettyXmlSerializer. (.getProperties cleaner))]
    (.getAsString serializer node)))
    
(defn find-node 
  "Returns the first zipper location for which
pred is true."
  [zipper pred]
  (loop [cur zipper]
    (when-not (z/end? cur)
      (if (pred cur)
        cur
        (recur (z/next cur))))))

(defn contains [s ptrn]
  (when s (.contains s ptrn)))

(defn save-to
  "Gets a URL or path of origin and copies the file
to the destintation."
  [path to]
  (println path to)
  (let [input  (io/input-stream path)
        output (io/output-stream to)]
    (loop [b (.read input)]
      (when (>= b 0)
        (.write output b)
        (recur (.read input))))
    (.close input)
    (.close output)))

(defn macanudo? 
  "Predicate that finds out if the zipper loc
contains the image with the macanudo preview."
  [loc]
  (let [node     (z/node loc)
        parent   (-> loc z/up)
        parent   (when parent (z/node parent))
        img?     (= :img (:tag node))
        parent-macanudo?
                 (and parent
                      (= :a (xml/tag parent))
                      (-> parent :attrs :title (contains "Macanudo")))]
    (and img? parent-macanudo?)))

(defn save-todays [path dir]
  (let [filename (-> (.. (SimpleDateFormat. "yyyy-MM-dd") (format (Date.)))
                     (str ".jpg"))]
    (save-to path (str dir filename))))

(def humor-url "http://www.lanacion.com.ar/humor")

(defn -main [target-dir & args]
  (-> humor-url
      clean-html
      xml-str
      z/xml-zip
      (find-node macanudo?)
      z/node
      :attrs
      :src
      (.replaceAll "w\\d*\\." ".")
      (save-todays target-dir)))