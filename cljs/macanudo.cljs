(ns macanudo)

(def current-page 0)
(def page-size 5)
(def comics-url "https://github.com/jfacorro/macanudo/blob/master/log/")

(defn get-by-id [id]
  (.getElementById js/document (name id)))

(defn set-attribute [elem [k v]]
  (.setAttribute elem (name k) v))

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

(defn add-comic [parent date]
  (let [y-m-d (format-date date [:y "-" :m "-" :d])
        d-m-y (format-date date [:d "/" :m "/" :y])
        src   (str comics-url y-m-d ".jpg?raw=true")
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

(defn next-page [])

(defn load-page [page]
  (let [image-list (get-by-id :image-list)
        start      (* current-page page-size)
        end        (+ start page-size)
        today      (dec-date (js/Date.) start)
        dates      (map (partial dec-date today) (range start end))]
    (doall (map #(add-comic image-list %) dates))))
  
(defn init []
  (load-page current-page))

(set! (.-onload js/window) init)