(ns juan.algo.pivot-price
  (:require
   [tablecloth.api :as tc]
   [tech.v3.datatype.functional :as fun]
   [tech.v3.dataset.rolling :refer [rolling]]))

(defn pivots-price-one [low-vec high-vec]
  (tc/dataset [{:name :p0-low :price (-> low-vec last)}
               {:name :p0-high :price (-> high-vec last)}
               {:name :p1-low :price (get low-vec -2)}
               {:name :p1-high :price (get high-vec -2)}
               {:name :pweek-high :price (apply fun/max high-vec)}
               {:name :pweek-low :price (apply fun/min low-vec)}]))

(defn pivots-price [env opts bar-ds]
  (:pivots-price (rolling bar-ds {:window-size 6
                                  :relative-window-position :left}
                 {:pivots-price {:column-name [:low :high]
                                 :reducer pivots-price-one
                                 :datatype :object}})))


(defn add-pivots-price [env opts bar-ds]
  (tc/add-column bar-ds :pivots-price (pivots-price env opts bar-ds)))


(comment 
  (require '[tech.v3.dataset :as tds])
  (def ds (tds/->dataset {:close (map #(Math/sin (double %))
                                      (range 0 200 0.1))
                          :high (map inc(map #(Math/sin (double %))
                                     (range 0 200 0.1)))
                          :low (map #(Math/sin (double %))
                                    (range 0 200 0.1))
                          :date (range 0 200 0.1)
                          
                          }))
  
  ds

  (pivots-price nil {} ds)
  
  (add-pivots-price nil {} ds)
 ; 
  )
