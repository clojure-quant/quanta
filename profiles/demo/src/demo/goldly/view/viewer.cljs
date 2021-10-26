

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


(defn debug-segment [url fun args]
  [:div.bg-gray-500.mt-5
   [:p.font-bold "segment debug ui"]
   [:p "fn: " fun]
   [:p "url: " url]
   [:p "args: " (pr-str args)]])

(defn url-nb-data [ns [id fmt]]
  (let [ext (case fmt
              :edn ".edn"
              :img ".png"
              :text ".txt"
              ".edn"
              )]
  (str "http://localhost:8000/api/viewer/file/" ns "/" id ext)))




(defn show-segment [ns {:keys [resources form] :as seg}]
  (let [;["img" {:box :lg} ["1" :png]]}]
        fun (first form)
        args (rest form)
        data (last args)
        normal-args (take (dec (count args)) args)
        render-fn (resolve-fun fun)
        url (url-nb-data ns data)]
    [:div.bg-blue-300.p-2.border.border-solid
     [render-fn url normal-args]
     (when show-viewer-debug-ui
       [debug-segment url fun normal-args])]))



(defn debug-notebook [nb]
  [:div.bg-gray-500.mt-5
   [:p.font-bold "notebook debug ui"]
   (pr-str nb)])


(comment
  {:ns "demo.playground.cljplot"
   :plots [{:resources [["1" :text] ["2" :png]]
            :form [:div
                   ["text" {:class "text-xl text-red-500 text-bold"}
                    ["1" :text]]
                   ["img" {:box :lg}
                    ["2" :png]]]}]}
;
  )


(defn view-notebook [{:keys [ns plots] :as nb}]
  [:div.bg-indigo-300.p-7
   [:h1.text-xl.text-blue-800.text-xl.pb-3 ns]
   (into [:div]
         (map #(show-segment ns %) plots))
   (when show-viewer-debug-ui
     [debug-notebook nb])])