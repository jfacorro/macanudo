(ns macanudo)

(def current-page 0)
(def page-size 5)
(def comics-url "https://raw.github.com/jfacorro/macanudo/master/log/")

(defn get-by-id [id]
  (.getElementById js/document (name id)))

(defn set-attribute [elem [k v]]
  (.setAttribute elem (name k) v))

(defn on [evt-name obj f]
  (aset obj (str "on" (name evt-name)) f))
  
(defn process-child [parent child]
  (cond (map? child)
          (doseq [attr-val child] (set-attribute parent attr-val))
        (string? child)
          (append parent (.createTextNode js/document child))
        (vector? child)
          (append parent (create-element child)))
  parent)
  
(defn create-element [[tag & xs]]
  (let [elem (.createElement js/document (name tag))]
    (doseq [child xs] (process-child elem child))
    elem))

(defn append [parent child]
  (.appendChild parent child))

(defn remove-all-children [elem]
  (aset elem "innerHTML" ""))

(defn add-comic [parent date]
  (let [y-m-d (format-date date [:y "-" :m "-" :d])
        d-m-y (format-date date [:d "/" :m "/" :y])
        src   (str comics-url y-m-d ".jpg")
        div   [:div [:h4 d-m-y] [:img {:src src}]]]
    (append parent (create-element div))))

(defn log [msg]
  (.log js/console msg))

(defn dec-date [date n]
  (let [date (js/Date. date)
        x    (-> date .getDate (- n))]
    (.setDate date x)
    date))

(defn leading-0 [n]
  (if (< n 10) 
    (str "0" n) 
    (str n)))

(defn format-date [date format]
  (let [y    (.getFullYear date)
        m    (-> (.getMonth date) inc leading-0)
        d    (-> (.getDate date) leading-0)
        date {:y y :m m :d d}]
    (apply str (map #(if (keyword? %) (% date) %) format))))

(defn next-page []
  (set! current-page (inc current-page))
  (load-page current-page))
  
(defn prev-page []
  (when (pos? current-page)
    (set! current-page (dec current-page))
    (load-page current-page)))
  
(defn load-page [page]
  (let [image-list (get-by-id :image-list)
        start      (* current-page page-size)
        end        (+ start page-size)
        today      (js/Date.)
        dates      (map (partial dec-date today) (range start end))]
    (log (str "start: " start "- end: " end))
    (remove-all-children image-list)
    (doall (map #(add-comic image-list %) dates))))
  
(defn init []
  (on :click (get-by-id :future) prev-page)
  (on :click (get-by-id :past) next-page)
  (load-page current-page))

(set! (.-onload js/window) init)