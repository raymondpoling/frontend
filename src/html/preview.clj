(ns html.preview
  (:require [html.header :refer [header]]
            [hiccup.page :refer [html5]]))

(defn stylesheet [path]
  [:link {:rel "stylesheet" :type "text/css" :href path}])

(defn make-options [schedule schedules]
  (map #(vector :option (when (= schedule %) {:selected "selected"}) %)
       schedules))

(defn form [options idx update formats]
  [:form {:action "preview.html" :method "get"}
    [:select {:name "schedule"}
      options]
    [:input {:type "hidden" :name "idx" :value idx}]
    [:input {:type "text" :name "index" :size 5 :value idx}]
    [:label {:for "update"} "Update?"]
   [:input {:type "checkbox" :id "update" :value "update"
            :name "update" :checked (when update "checked")}]
   [:select {:name "select-format" :id "select-format"}
    (map #(vector :option %) formats)]
    [:input {:type "submit" :value "Preview"}]
    [:input {:type "submit" :name "reset" :value "Reset"}]
    [:input {:type "submit" :name "download" :value "Download"}]])

(defn preview-column [schedule divs idx & outline]
  [:div {:class "column"
         :style (when (first outline)
                  "border:solid black 1px;border-radius: 0.5em")}
    [:h2 schedule ": " idx]
    divs])

(defn make-title [i]
  (str (:series i) " S" (:season i) "E" (:episode i)))

(defn make-divs [items]
  (map #(vector :article {:class "item" }
                [:img {:src (if (not (or (empty? (:thumbnail %))
                                         (= "N/A" (:thumbnail %))))
                              (:thumbnail %)
                              "/image/not-available.svg")}]
                [:ul {:class "textbox"}
                 [:li {:class "index"}
                  (get-in % [:playlist :name]) ": "
                  (get-in % [:playlist :index])]
                 [:li (if (seq (:imdbid %))
                        [:a {:href (str "http://imdb.com/title/" (:imdbid %))
                             :target "_blank"} (make-title %)]
                        (make-title %))]
                 [:li [:em (:episode_name %)]]]
                [:div {:class "summary"}
                 [:hr]
                 [:p (:summary %)]])
       items))

  (defn make-preview-page [schedule schedules start-idx update preview formats
                           role]
  (let [options (make-options schedule schedules)
        cols (map #(vector (make-divs %1) %2) preview
                  (range start-idx
                         (+ (count preview) start-idx)))
        midpoint  (+ start-idx
                     (int (Math/floor
                           (/ (count cols) 2))))]
    (html5 {:lang "en" :dir "ltr"}
      [:head
        [:title "Schedule Preview"]
        (stylesheet "css/master.css")
        (stylesheet "css/preview.css")]
      [:body
        [:div {:id "content"}
          (header "Schedule Preview" role)
         (form options midpoint update formats)
         (map (fn [[items idx]] (preview-column
                                 schedule items idx
                                 (when (= idx midpoint) true)))
                cols)]])))
