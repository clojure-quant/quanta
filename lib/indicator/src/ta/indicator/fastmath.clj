(ns ta.indicator.fastmath
  (:require
   [fastmath.signal :as s]))

(defn lowpass [{:keys [rate cutoff]
                :or {rate 44100.0 cutoff 2000.0}} v]
  (let [lp (s/effect :simple-lowpass {:rate rate :cutoff cutoff})]
    (s/apply-effects v lp)))

(defn highpass [{:keys [rate cutoff]
                 :or {rate 44100.0 cutoff 2000.0}} v]
  (let [lp (s/effect :simple-highpass {:rate rate :cutoff cutoff})]
    (s/apply-effects v lp)))

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
;  
  )



