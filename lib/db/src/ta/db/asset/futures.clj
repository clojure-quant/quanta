(ns ta.db.asset.futures)

;; Why is this here?

;; In our instrument db we dont store all available futures with their expiry dates.
;; If the symbol is a specific future, we search the base symbol and then get back the adjusted 
;; provider symbol for this expiry date.

;; Continuous future
;; NG0 = Natural Gas

(defn is-future? [s]
  (when-let [m (re-matches #"(.*)(\d\d)(\d\d.*)" s)]
    (let [[_ symbol month year] m]
      {:symbol symbol
       :month (parse-long month)
       :year (parse-long year)
       :symbol-root (str symbol "0")})))

(defn future-extension [year month]
  (str (case month
         1 "F"  2 "G"  3 "H"
         4 "J"  5 "K"  6 "M"
         7 "N"  8 "Q"  9 "U"
         10 "V" 11 "X" 12 "Z")
       year))

(defn future-symbol [{:keys [month year symbol]} ; output of is-future?
                     {:keys [kibot] :as db-data} ; from symbol db
                     ]
  (let [db-symbol (str symbol month year)]
    (assoc db-data
           :kibot (str kibot (future-extension year month))
           :symbol db-symbol)))

(comment
  (is-future? "NG1223")
  ;; => {:symbol "NG", :month 12, :year 23, :symbol-root "NG0"}
  (is-future? "QQQ")
  ;; => nil
  (is-future? "NG0")
  ;; => nil

  (future-extension 23 12)
 ;; => "Z23"

  ;; => {:symbol "NG0", :kibot "NG", :name "CONTINUOUS NATURAL GAS CONTRACT", :category :future, :exchange "SG"}
  (future-symbol {:symbol "NG", :month 12, :year 23}
                 {:symbol "NG0", :kibot "NG", :name "CONTINUOUS NATURAL GAS CONTRACT",
                  :category :future, :exchange "SG"})

; 
  )




