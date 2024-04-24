(ns ta.indicator.fastmath
  (:require
   [tech.v3.dataset :as tds]
   [tablecloth.api :as tc]
   [fastmath.signal :as s]
   [fastmath.stats :as stats]))

(defn lowpass
  "A lowpass filter has a similar smoothing effect as a Moving Average function, 
   but produces a better reproduction of the price curve and has less lag. 
   This means the return value of a lowpass filter function isn't as delayed 
   as the return values of Simple Moving Average or EMA functions that are 
   normally used for trend trading. The script can react faster on price 
   changes, and thus generate better profit."
  [{:keys [rate cutoff]
    :or {rate 44100.0 cutoff 2000.0}} v]
  (let [lp (s/effect :simple-lowpass {:rate rate :cutoff cutoff})]
    (s/apply-effects v lp)))

; var LowPass(var Data,int Period)
; {
; var LP = series(Data[0]);
; var a = 2.0/(1+Period);
; return LP[0] = (a-0.25aa)Data[0]
; + 0.5aaData[1]
; - (a-0.75aa)Data[2]
; + 2(1.-a)LP[1]
; - (1.-a)(1.-a)*LP[2];
; }

(defn highpass [{:keys [rate cutoff]
                 :or {rate 44100.0 cutoff 2000.0}} v]
  (let [lp (s/effect :simple-highpass {:rate rate :cutoff cutoff})]
    (s/apply-effects v lp)))

(defn round
  [n scale rm]
  (.setScale ^java.math.BigDecimal (bigdec n)
             (int scale)
             ^RoundingMode (if (instance? java.math.RoundingMode rm)
                             rm
                             (java.math.RoundingMode/valueOf
                              (str (if (ident? rm) (symbol rm) rm))))))

(defn correlations-dataset
  "data can be a tml-dataset or a seq of tml-columns
   columns needs to be a vector of column names."
  [data columns]
  (let [col-vecs (if (tds/dataset? data)
                   (mapv #(get data %) columns)
                   (into [] data))
        matrix (stats/correlation-matrix col-vecs)]
    (->> matrix
         (map-indexed
          (fn [i row]
            (let [coli (columns i)]
              (->> row
                   (map-indexed
                    (fn [j corr]
                      (let [colj (columns j)]
                        {:i i
                         :j j
                         :coli coli
                         :colj colj
                         :corr corr
                         #_:corr-round #_(round corr 2 :HALF_EVEN)})))))))
         (apply concat)
         tc/dataset)))

(defn vegalite-correlation
  "creates a vegalite plot from a correlations-ds.
   optional opts are merged into vegalite spec
   (useful to change plot-size)"
  ([ds-correlation]
   (vegalite-correlation ds-correlation {}))
  ([ds-correlation opts]
   (let [data-clj (->> ds-correlation
                       (tds/mapseq-reader)
                       (into []))]
     ^{:render-fn 'ui.vega/vegalite}
     {:spec (merge {:$schema "https://vega.github.io/schema/vega-lite/v5.json"
                    :mark "rect"
                    :data {:values data-clj}
                    :encoding {:x {:field "coli" :type "ordinal"}
                               :y {:field "colj" :type "ordinal"}
                               :color {:field "corr" :type "quantitative"
                                       :scale {:scheme "redblue"
                                               :domain [-1.0 1.0]}}}} opts)})))

(comment
  s/effects-list
  ;;=> (:bandwidth-limit :basstreble :biquad-bp
;;=>                   :biquad-eq :biquad-hp
;;=>                   :biquad-hs :biquad-lp
;;=>                   :biquad-ls :decimator
;;=>                   :distort :divider
;;=>                   :dj-eq :echo
;;=>                   :fm :foverdrive
;;=>                   :mda-thru-zero :phaser-allpass
;;=>                   :simple-highpass :simple-lowpass
;;=>                   :slew-limit :vcf303)
 ; 
  (let [lp (s/effect :simple-lowpass)]
    (lp 0.5)
    (lp 0.5)
    (lp 0.5))

  (-> (lowpass {} (range 100)))

  (-> (highpass {} (range 100)))

  (round (/ 2.0 3) 2 :DOWN)
  (round (/ 2.0 3) 2 :UP)
  (round (/ 2.0 3) 2 :HALF_EVEN)

  (def ds (tc/dataset {:a [1.0 5.0 3.0]
                       :b [2.0 3.0 4.0]
                       :c [5.0 2.0 1.0]}))

  (correlations-dataset ds [:a :b :c])

  (correlations-dataset [(:a ds) (:b ds) (:c ds)]
                        [:a :b :c])

;  
  )



