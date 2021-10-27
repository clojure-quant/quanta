


;; SEGMENT 

(defn debug-segment [fmt url fun args]
  [:div.bg-gray-500.mt-5
   [:p.font-bold "segment debug ui"]
   [:p "fn: " fun]
   [:p "fmt: " fmt]
   [:p "url: " url]
   [:p "args: " (pr-str args)]])

(defn fmt->ext [fmt]
  (case fmt
    :edn ".edn"
    :png ".png"
    :text ".txt"
    ".edn"))


(defn url-nb-data [ns [id fmt]]
  (let [ext (fmt->ext fmt)]
  (str "http://localhost:8000/api/viewer/file/" ns "/" id ext)))

(defn show-segment [ns {:keys [resources form] :as seg}]
  (let [;["img" {:box :lg} ["1" :png]]}]
        fun (first form)
        args (rest form)
        data (last args)
        normal-args (take (dec (count args)) args)
        render-fn (resolve-fun fun)
        fmt (second data)
        url (url-nb-data ns data)]
    [:div.bg-blue-300.p-2.border.border-solid
     [render-fn url normal-args]
     (when show-viewer-debug-ui
       [debug-segment fmt url fun normal-args])]))

;; NOTEBOOK

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

;; VIEWER APP

(defn debug-viewer-state []
  [:div.bg-gray-500.pt-10.hoover-bg-blue-300
   [:p.font-bold "viewer debug ui"]
   [:p (-> @viewer-state pr-str)]])


(defn nb-chooser [nb-name]
  [:span.m-1.border.p-1 
   {:on-click #(swap! viewer-state assoc :current nb-name)}
     nb-name])


(defn notebook-list [notebook-names]
  (into 
     [:div]
     (map nb-chooser notebook-names)
   ))


(defn viewer-app []
  (fn []
    (let [{:keys [current notebooks]} @viewer-state
          nb (get notebooks current)
          notebook-names (-> @viewer-state :notebooks keys)]
    [:div 
      ;[:div "notebooks:" (pr-str notebook-names)]
      [notebook-list notebook-names]
      [view-notebook nb]
      (when show-viewer-debug-ui
        [debug-viewer-state])])))
    