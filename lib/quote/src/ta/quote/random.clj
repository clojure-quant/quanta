(ns ta.quote.random
  "random quote feed
   useful because always open, and limited potential for strange errors"
  (:require
   [taoensso.timbre :as timbre :refer [info warn error]]
   [ta.quote.core :refer [quotefeed create-stream! publish! get-stream]]))

(defn zero-mean-random-value []
  (* (- (rand 0.005) 0.0025) 10.0))

(defn random-return-value []
   ;; mimicing a normal distribution
  (apply + (repeatedly 10 zero-mean-random-value)))

(defn random-return-multiplyer []
  (+ 1.0 (random-return-value)))

(defn next-price [p]
  (* p (random-return-multiplyer)))

(defn price-seq [start-price]
  (iterate next-price start-price))

(comment
  (zero-mean-random-value)
  (random-return-multiplyer)
  (take 10 (price-seq 100.0))
;  
  )

(defn set-interval [callback ms]
  (future (while true (do (Thread/sleep ms) (callback)))))

;(def job (set-interval #(println "hello") 1000))
; (future-cancel job)

(defn symbol->quote [[asset price]]
  {:asset asset
   :price price
   :size 100})

(defn feed-publish-quotes! [{:keys [state] :as this}]
  (let [symbols (:symbols @state)
        quote-messages (map symbol->quote symbols)
        publish-msg! (partial publish! this)]
    (doall 
     (map publish-msg! quote-messages))))

(defn add-asset [state asset]
  (swap! state update-in [:symbols] assoc asset 100.0))

(defn update-price-asset [state asset]
  (swap! state update-in [:symbols asset] next-price))


(defn gen-change-prices [{:keys [state] :as this}]
  (fn []
    ;(info "generating prices..")
    (doall
     (map (partial update-price-asset state)
          (keys (:symbols @state))))
    ;(info "price map: " (:symbols @state))
    ;(info "publishing quotes..")
    (feed-publish-quotes! this)
    ;(info "publishing quotes.. done!")
    ))


(defrecord quotefeed-random [opts state]
  quotefeed
  (connect [this]
    (info "random connect: " (:opts this))
    (let [f (set-interval (gen-change-prices this) 500)]
      (swap! (:state this) assoc :f f)))
  (disconnect [this]
    (info "random disconnect: " (:opts this))
    (info "state: " @(:state this))
    (future-cancel (:f @(:state this)))
    (swap! (:state this) assoc :symbols {})
    (swap! (:state this) dissoc :f))
  (subscribe [this asset]
    (info "random subscribe: " asset)
    (add-asset (:state this) asset))
  (unsubscribe [this asset]
    (info "random unsubscribe: " asset)
    (swap! (:state this) update-in [:symbols] dissoc asset 100.0))
  (quote-stream [this]
    (get-stream this)))

(defn create-quotefeed-random [opts]
  (let [feed (quotefeed-random. opts (atom {:symbols {}}))]
    (create-stream! feed)
    feed
    ))

;; in demo see: notebook.live.random-quotes





