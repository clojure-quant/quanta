

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