(ns ta.math.bin
  "examples: notebook.math.bin")

(defn bin-full
  "bins values in vector xs, returns a vector with bin-ids.
   the number of bins can be either fixed {:n 5}
   or it can vary based on the bin-height {:step 0.1}"
  [{:keys [n step] :as opts} xs]
  (assert (or n step) "binner opts require :bin-number or :bin-step key")
  (let [min-x    (apply min xs)
        max-x    (apply max xs)
        min-x (if n
                min-x
                (-> (/ min-x step)
                    (Math/ceil)
                    (int)
                    (* step)))
        max-x (if n
                max-x
                (-> (/ max-x step)
                    (Math/ceil)
                    (int)
                    (inc)
                    (* step)))
        range-x  (- max-x min-x)
        n-bins (if n
                 n
                 (-> (/ range-x step)
                     (Math/ceil)
                     (int)))
        bin-fn   (fn [x]
                   (-> x
                       (- min-x)
                       (/ range-x)
                       (* n-bins)
                       (int)
                       (min (dec n-bins))))]
    {:n-bins n-bins
     :min-x min-x
     :max-x max-x
     :range-x range-x
     :bin-fn bin-fn
     :result (map bin-fn xs)}))

(defn bin-lower-bound [{:keys [n-bins min-x max-x range-x]} b]
  (-> b
      (* range-x)
      (/ n-bins)
      (+ min-x)))

(defn bin-upper-bound [{:keys [n-bins min-x max-x range-x]} b]
  (-> b
      inc
      (* range-x)
      (/ n-bins)
      (+ min-x)))

(defn bin-middle [{:keys [n-bins min-x max-x range-x]} b]
  (-> b
      (+ 0.5)
      (* range-x)
      (/ n-bins)
      (+ min-x)))

(defn bin-result [r]
  (:result r))

(defn bin [opts xs]
  (-> (bin-full opts xs) :result))
