(ns quanta.studio.page.algo
  (:require
   [spaces.core]
   [options.edit :as edit]
   [ta.viz.lib.ui :refer [link-href]]
   [quanta.studio.view.state :as s]
   [quanta.studio.view.result :refer [result-view]]
   [quanta.studio.view.options :refer [options-ui]]))

;; header

(defn keyword->spec [kw]
  {:id  kw
   :name (str kw)})

(defn keywords->spec [kws]
  (map keyword->spec kws))

(defn header-ui [state]
  (s/get-available-templates state)
  (let [template-a (s/get-view-a state :template)
        template-list-a (s/get-view-a state :template-list)]
    (fn [state]
      [:div.w-full.h-full.bg-blue-300
       [link-href "/" "main"]
       ; template selector
       ;[:p "template-selector" (pr-str @template-list-a)]
       [edit/select
        {:set-fn (fn [template-id]
                   (s/set-state state :current {})
                   (s/set-state state :options {})
                   (s/set-state state :template template-id)
                   (s/get-template-options state template-id))
         :options {:spec (keywords->spec @template-list-a)
                   :class "bg-green-500"}}
        @template-a]
       ; options for selected template
       [options-ui state]])))

(defn algo-ui []
  (let [state (s/create-state)]
    (fn []
      [spaces.core/viewport
       [spaces.core/top-resizeable {:size 50}
        ;[:div.bg-gray-200.w-full.h-full "top"]
        [header-ui state]]
       [spaces.core/fill
        ;[:div.bg-red-200.w-full.h-full "main"]
        [result-view state]]])))

(defn algo-page [_route]
  [algo-ui])
