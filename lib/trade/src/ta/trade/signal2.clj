(ns ta.trade.signal2)

; position

(defn position? [p]
  (not (= :flat (:side p))))

; entry

(defn entry? [row]
 (contains? #{:long :short} (:signal row)))

(defn entry-position [asset position-sizer {:keys [signal close idx date] :as row} ]
 {:side signal
  :qty (position-sizer close)
  :entry-idx idx
  :entry-price close
  :entry-date date})



(defn exit? [Row]

)





(defn default-exit [bar]
  {:exit-price (:close bar)
   :exit-time  (:end-zdt bar)
   :exit-index (:index bar)})




defn hold-for-exit [{:keys [position current-bar] :as ctx} n]
  (cond-> ctx
    ;;(and position (= (:bars-held position) n))
    (and position (= (:index current-bar) (+ n (:entry-index position))))
    ;;what if pending exit already exists?
    (update :pending-exit merge (default-exit current-bar))))

Ctx: 
current-bar
Position
pending-entry
pending-exit

(defn run-strat [bars handler]
  (assert (vector? bars))
  (let [bars (vec (doall (map-indexed (fn [i x] (assoc x :index i)) bars)))]
    (reduce
     (fn [ctx bar]
       (handler (assoc ctx :current-bar bar)))
     {:trades []
      :bars   bars}
     bars)))

(defprotocol position-db
  (get-actual-position [db asset])
  (set-actual-position [db asset side qty entry-price entry-date])
  (set-target-position [db asset side qty])
  

(defn track-position [env bar-ds asset idx target-position]
 (let [pm (position-manager env)
        p (position/actual-position pm asset)]
   (position/set-actual-position pm asset 
     {:entry-price (:close bar-ds)
      :entry-idx idx
]








robot
Env: 
broker (account credentials; socket; order-stream orderupdate-stream execution-stream)
Position-db (

Target.    Target.      -> 
Signal -> position.    -> trade
Kw.         Map
               :asset
               :qty
               :entry-px
               :entry-idx
               :entry-date

(Get-actual-position {:asset asset})


(defn trade-current-bar [env bar-ds idx asset]
  (let [signal (:signal bar-ds)
         pm (:pm env)
         pos (position/get-position pm asset)




Trailing-sl
For each position. While position is open.
Modify trailing-series or create multiple trailing-s lines.




Instruktionen
Open-position side
Close-position 
Take-profit price
Stop-loss price
Trailing-stop-loss price


Algo calculated auf trailing window.
Trailing window kann entries generieren.


(defn exit-time [env opts {:keys [position position-history-ds idx]]
   (if (> idx (+ (:entry-idx position) (:exit-time opts)))
       :flat
       Position)))

(defn exit-profit [env opts {:keys [position position-history-ds idx]]
   (if (>= (:high ds) (* (:entry-price position) 1.05)
       :flat
       Position)))

(defn exit-loss [env opts {:keys [position position-history-ds idx]]
   (if (<= (:low ds) (* (:entry-price position) 0.95)
       :flat
       Position)))

;; ./clj-kondo/config.edn
{:lint-as 
 {promesa.core/let clojure.core/let}}



Ich verwende aktuell:
signal->roundtrips was eigentlich position->roundtrips heissen müsste.


(defn signal->position
  [{:keys [exit-rule
               entry-rule
               entry-position-sizer]}]
  (let [roundtrips (volatile [])
         addrt (fn [p] (vswap! Roundtrips p))
 (indicator 
  [position (volatile {:side :flat})
   Open-pos (fn [p] (vreset! position p)
    (fn [row]
       (If (and (position? @position)
            (exit? @position row))
       (exit? row)
       
       (If (entry? row) 
       (open-position (entry asset row position-sizer)))
