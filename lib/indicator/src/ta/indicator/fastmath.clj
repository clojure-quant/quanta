(ns ta.indicator.fastmath
  (:require
   [fastmath.signal :as s]))

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



