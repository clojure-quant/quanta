(ns juan.algo.pivot-price
  (:require
   [tablecloth.api :as tc]
   [tech.v3.datatype :as dtype]
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

(defn pivot-count [pivots]
  (dtype/emap tc/row-count :int64 pivots))
  


(defn add-pivots-price [env opts bar-ds]
  (let [pivots (pivots-price env opts bar-ds)]
    (tc/add-columns bar-ds {:pivots-price pivots
                            :ppivotnr (pivot-count pivots)})  
    )
  )


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
  ;; => #tech.v3.dataset.column<dataset>[2001]
  ;;    :pivots-price
  ;;    [_unnamed [6 2]:
  ;;    
  ;;    |       :name | :price |
  ;;    |-------------|-------:|
  ;;    |     :p0-low |    0.0 |
  ;;    |    :p0-high |    1.0 |
  ;;    |     :p1-low |    0.0 |
  ;;    |    :p1-high |    1.0 |
  ;;    | :pweek-high |    1.0 |
  ;;    |  :pweek-low |    0.0 |
  ;;    , _unnamed [6 2]:
  ;;    
  ;;    |       :name |     :price |
  ;;    |-------------|-----------:|
  ;;    |     :p0-low | 0.09983342 |
  ;;    |    :p0-high | 1.09983342 |
  ;;    |     :p1-low | 0.00000000 |
  ;;    |    :p1-high | 1.00000000 |
  ;;    | :pweek-high | 1.09983342 |
  ;;    |  :pweek-low | 0.00000000 |
  ;;    , _unnamed [6 2]:
  ;;    
  ;;    |       :name |     :price |
  ;;    |-------------|-----------:|
  ;;    |     :p0-low | 0.19866933 |
  ;;    |    :p0-high | 1.19866933 |
  ;;    |     :p1-low | 0.09983342 |
  ;;    |    :p1-high | 1.09983342 |
  ;;    | :pweek-high | 1.19866933 |
  ;;    |  :pweek-low | 0.00000000 |
  ;;    , _unnamed [6 2]:
  ;;    
  ;;    |       :name |     :price |
  ;;    |-------------|-----------:|
  ;;    |     :p0-low | 0.29552021 |
  ;;    |    :p0-high | 1.29552021 |
  ;;    |     :p1-low | 0.19866933 |
  ;;    |    :p1-high | 1.19866933 |
  ;;    | :pweek-high | 1.29552021 |
  ;;    |  :pweek-low | 0.00000000 |
  ;;    , _unnamed [6 2]:
  ;;    
  ;;    |       :name |     :price |
  ;;    |-------------|-----------:|
  ;;    |     :p0-low | 0.38941834 |
  ;;    |    :p0-high | 1.38941834 |
  ;;    |     :p1-low | 0.29552021 |
  ;;    |    :p1-high | 1.29552021 |
  ;;    | :pweek-high | 1.38941834 |
  ;;    |  :pweek-low | 0.00000000 |
  ;;    , _unnamed [6 2]:
  ;;    
  ;;    |       :name |     :price |
  ;;    |-------------|-----------:|
  ;;    |     :p0-low | 0.47942554 |
  ;;    |    :p0-high | 1.47942554 |
  ;;    |     :p1-low | 0.38941834 |
  ;;    |    :p1-high | 1.38941834 |
  ;;    | :pweek-high | 1.47942554 |
  ;;    |  :pweek-low | 0.00000000 |
  ;;    , _unnamed [6 2]:
  ;;    
  ;;    |       :name |     :price |
  ;;    |-------------|-----------:|
  ;;    |     :p0-low | 0.56464247 |
  ;;    |    :p0-high | 1.56464247 |
  ;;    |     :p1-low | 0.47942554 |
  ;;    |    :p1-high | 1.47942554 |
  ;;    | :pweek-high | 1.56464247 |
  ;;    |  :pweek-low | 0.09983342 |
  ;;    , _unnamed [6 2]:
  ;;    
  ;;    |       :name |     :price |
  ;;    |-------------|-----------:|
  ;;    |     :p0-low | 0.64421769 |
  ;;    |    :p0-high | 1.64421769 |
  ;;    |     :p1-low | 0.56464247 |
  ;;    |    :p1-high | 1.56464247 |
  ;;    | :pweek-high | 1.64421769 |
  ;;    |  :pweek-low | 0.19866933 |
  ;;    , _unnamed [6 2]:
  ;;    
  ;;    |       :name |     :price |
  ;;    |-------------|-----------:|
  ;;    |     :p0-low | 0.71735609 |
  ;;    |    :p0-high | 1.71735609 |
  ;;    |     :p1-low | 0.64421769 |
  ;;    |    :p1-high | 1.64421769 |
  ;;    | :pweek-high | 1.71735609 |
  ;;    |  :pweek-low | 0.29552021 |
  ;;    , _unnamed [6 2]:
  ;;    
  ;;    |       :name |     :price |
  ;;    |-------------|-----------:|
  ;;    |     :p0-low | 0.78332691 |
  ;;    |    :p0-high | 1.78332691 |
  ;;    |     :p1-low | 0.71735609 |
  ;;    |    :p1-high | 1.71735609 |
  ;;    | :pweek-high | 1.78332691 |
  ;;    |  :pweek-low | 0.38941834 |
  ;;    , _unnamed [6 2]:
  ;;    
  ;;    |       :name |     :price |
  ;;    |-------------|-----------:|
  ;;    |     :p0-low | 0.84147098 |
  ;;    |    :p0-high | 1.84147098 |
  ;;    |     :p1-low | 0.78332691 |
  ;;    |    :p1-high | 1.78332691 |
  ;;    | :pweek-high | 1.84147098 |
  ;;    |  :pweek-low | 0.47942554 |
  ;;    , _unnamed [6 2]:
  ;;    
  ;;    |       :name |     :price |
  ;;    |-------------|-----------:|
  ;;    |     :p0-low | 0.89120736 |
  ;;    |    :p0-high | 1.89120736 |
  ;;    |     :p1-low | 0.84147098 |
  ;;    |    :p1-high | 1.84147098 |
  ;;    | :pweek-high | 1.89120736 |
  ;;    |  :pweek-low | 0.56464247 |
  ;;    , _unnamed [6 2]:
  ;;    
  ;;    |       :name |     :price |
  ;;    |-------------|-----------:|
  ;;    |     :p0-low | 0.93203909 |
  ;;    |    :p0-high | 1.93203909 |
  ;;    |     :p1-low | 0.89120736 |
  ;;    |    :p1-high | 1.89120736 |
  ;;    | :pweek-high | 1.93203909 |
  ;;    |  :pweek-low | 0.64421769 |
  ;;    , _unnamed [6 2]:
  ;;    
  ;;    |       :name |     :price |
  ;;    |-------------|-----------:|
  ;;    |     :p0-low | 0.96355819 |
  ;;    |    :p0-high | 1.96355819 |
  ;;    |     :p1-low | 0.93203909 |
  ;;    |    :p1-high | 1.93203909 |
  ;;    | :pweek-high | 1.96355819 |
  ;;    |  :pweek-low | 0.71735609 |
  ;;    , _unnamed [6 2]:
  ;;    
  ;;    |       :name |     :price |
  ;;    |-------------|-----------:|
  ;;    |     :p0-low | 0.98544973 |
  ;;    |    :p0-high | 1.98544973 |
  ;;    |     :p1-low | 0.96355819 |
  ;;    |    :p1-high | 1.96355819 |
  ;;    | :pweek-high | 1.98544973 |
  ;;    |  :pweek-low | 0.78332691 |
  ;;    , _unnamed [6 2]:
  ;;    
  ;;    |       :name |     :price |
  ;;    |-------------|-----------:|
  ;;    |     :p0-low | 0.99749499 |
  ;;    |    :p0-high | 1.99749499 |
  ;;    |     :p1-low | 0.98544973 |
  ;;    |    :p1-high | 1.98544973 |
  ;;    | :pweek-high | 1.99749499 |
  ;;    |  :pweek-low | 0.84147098 |
  ;;    , _unnamed [6 2]:
  ;;    
  ;;    |       :name |     :price |
  ;;    |-------------|-----------:|
  ;;    |     :p0-low | 0.99957360 |
  ;;    |    :p0-high | 1.99957360 |
  ;;    |     :p1-low | 0.99749499 |
  ;;    |    :p1-high | 1.99749499 |
  ;;    | :pweek-high | 1.99957360 |
  ;;    |  :pweek-low | 0.89120736 |
  ;;    , _unnamed [6 2]:
  ;;    
  ;;    |       :name |     :price |
  ;;    |-------------|-----------:|
  ;;    |     :p0-low | 0.99166481 |
  ;;    |    :p0-high | 1.99166481 |
  ;;    |     :p1-low | 0.99957360 |
  ;;    |    :p1-high | 1.99957360 |
  ;;    | :pweek-high | 1.99957360 |
  ;;    |  :pweek-low | 0.93203909 |
  ;;    , _unnamed [6 2]:
  ;;    
  ;;    |       :name |     :price |
  ;;    |-------------|-----------:|
  ;;    |     :p0-low | 0.97384763 |
  ;;    |    :p0-high | 1.97384763 |
  ;;    |     :p1-low | 0.99166481 |
  ;;    |    :p1-high | 1.99166481 |
  ;;    | :pweek-high | 1.99957360 |
  ;;    |  :pweek-low | 0.96355819 |
  ;;    , _unnamed [6 2]:
  ;;    
  ;;    |       :name |     :price |
  ;;    |-------------|-----------:|
  ;;    |     :p0-low | 0.94630009 |
  ;;    |    :p0-high | 1.94630009 |
  ;;    |     :p1-low | 0.97384763 |
  ;;    |    :p1-high | 1.97384763 |
  ;;    | :pweek-high | 1.99957360 |
  ;;    |  :pweek-low | 0.94630009 |
  ;;    ...]

  
  (add-pivots-price nil {} ds)
 ; 
  )
