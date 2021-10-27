;; comes from pinkie
;; but we need more customization!


(defn line-with-br [t]
  [:div
   [:span.font-mono.whitespace-pre t]
   [:br]])

(defn text2
  "Render text (as string) to html
   works with \\n (newlines)
   Needed because \\n is meaningless in html"
  ([t]
   (text2 {} t))
  ([opts t]
   (let [lines (str/split t #"\n")]
     (into
      [:div (merge {:class "textbox text-lg"} opts)]
      (map line-with-br lines)))))


;; render functions 

(defn img [url args]
  [:img.p-4 {:src url}])


(defn text-data [data args]
  [text2 (or (first args) {}) data])

(defn text-url [url args]
  [:div.p-4
   ;test if text2 works
   ;[text2 {:class "bg-blue-300 text-red-500"} "asdf\nasdf\n"]
   [url-loader url text-data args]])

; ["img" ["1" :png]]

(defn fun-unknown [fun]
  (fn [url args]
    [:div.bg-red-300.p-4
     [:p "unknown renderer: " fun]
     [:p "url: " url]
     [:p "args: " (pr-str args)]]))

(defn resolve-fun [fun]
  (case fun
    "img" img
    "text" text-url
    (fun-unknown fun)))