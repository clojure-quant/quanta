(ns quanta.notebook.model-javelin
  (:require
   [de.otto.nom.core :as nom]
   [javelin.core-clj :refer [cell cell=]]))

(defn start []
  (let [a (cell 0)              ;; input cell with initial value of 0.
        b (cell= (inc a))       ;; formula cell of a+1.
        c (cell= (+ 123 a b))]  ;; formula cell of a+b+123.
    (cell= (println c)) ;; anonymous formula cell for side effects.
    ;; c's initial value, 124, is printed.
    (swap! a inc)
    ;; a was incremented, and its new value propagated (consistently)
    ;; through b and c.  c's new value, 126, is printed to the console.
    :done))

(start)

; prints:
; 124
; 126
; :done

(defn divide [a b]
  (println "dividing " a " by " b)
  (if (= b 0)
    (nom/fail ::division {:message "division by 0"})
    (/ a b)))


(defn start2 []
  (let [a (cell 100)
        b (cell 2)
        c (cell= (divide a b))]
    (cell= (println "a: " a "b: " b "result: " c))
    (swap! a inc)
    (swap! a inc)
    (swap! b inc)
    (reset! b 0)
    (reset! b 2)
    :done))

(start2)



(require '[quanta.model.javelin.cell :refer [formula-cell value-cell]]
 )

(def A (value-cell nil 10))
(def B (value-cell nil 2))
(def C (formula-cell nil / [A B]))

@A
@B
@C

(reset! A 100)
(reset! B 0)

