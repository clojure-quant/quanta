
(def show-viewer-debug-ui true)

; (get-edn "/r/repl/bongo.edn" state [:data])

;http://localhost:8000/api/viewer/file/demo.playground.cljplot/1.txt

(defn load-url [url a]
  (if url
    (when (not (= url (:url @a)))
      (info (str "load-url: " url))
      (swap! a assoc :url url)
      (http/get-str url a [:data])
      nil)
    (swap! a assoc :data nil)))

(defn debug-loader [url data args]
  [:div.bg-gray-500.mt-5
   [:p.font-bold "loader debug ui"]
   [:p "url: " url]
   [:p "args: " (pr-str args)]
   [:p "data: " data]])

(defn url-loader [url fun args]
  (let [a (r/atom {:data nil
                   :url nil})]
    (fn [url fun args]
      (load-url url a)
      (if-let [d (:data @a)]
        [:div
         [error-boundary
          (if (empty? args)
            (fun d)
            (apply fun d args))]
         (when show-viewer-debug-ui
           [debug-loader url d args])]
        [:div "loading: " url]))))