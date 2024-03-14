(ns ta.interact.view.asset-picker)


(defn editor-asset-picker [{:keys [set-fn options]} current-val]
  (let [{:keys [class name] :or {class "" name ""}} options]
    [:input {:class class
             :type "text"
             :value current-val
             :placeholder (str "Asset: " name)
             :on-change (fn [e]
                          (let [v (-> e .-target .-value)]
                            (println "setting checkbox to: " v)
                            (set-fn v)))}]))