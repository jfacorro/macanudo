(ns macanudo)

(def current-page 0)
(def page-size 5)
(def comics-url "https://github.com/jfacorro/macanudo/blob/master/log/")

(defn get-by-id [id]
  (.getElementById js/document (name id)))

(defn set-attribute [elem [k v]]
  (.setAttribute elem (name k) v))

(defn create-element [tag attrs]
  (let [elem (.createElement js/document (name tag))]
    (reduce set-attribute elem attrs)
    elem))

(defn add-comic [parent date]
  (let [src (str comics-url date ".jpg?raw=true")
        img (create-element :img {:src src})]
    (.appendChild parent img)))

(defn log [msg]
  (.log js/console msg))

(defn dec-date [date n]
  (let [x (-> date .getDate (- n))]
    (.setDate date x)
    date))

(defn leading-0 [n]
  (if (< n 10) 
    (str "0" n) 
    (str n)))

(defn format-date [d]
  (let [y  (.getFullYear d)
        M  (-> (.getMonth d) inc leading-0)
        D  (-> (.getDate d) leading-0)]
    (str y "-" M "-" D)))

(defn next-page [])

(defn load-page [page]
  (let [image-list (get-by-id :image-list)
        start      (* current-page page-size)
        today      (dec-date (js/Date.) start)
        dates      (map (comp format-date (partial dec-date today)) (repeat page-size 1))]
    (doall (map #(add-comic image-list %) dates))))
  
(defn init []
  (load-page current-page))

(set! (.-onload js/window) init)