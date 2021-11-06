# visual studio code

add this extensions
- calva
- kondo

# 2021 10 - bollinger strategy backtest

bollinger band strategy:
assumption is, that crossing lower-bollinger statistically means we will go up.

bollinger-event-analysis
event index: :above=true and :above-count=1
for each bollinger-evnet:
- create walk-forward window (if it is possible
- find min/max in walk-forward window 
- normalize min/max with range=bollinger-up - bollinger-down
the bollinger band is significant if range is squed

optimize for best parameter

event bollinger cross     ==> liste of event-bollinger-cross
cross-type #{:up :down}
up
down
up%
down%
diff   (up-down)
diff%  (up% - down%)

target funktion
for cross-type-up: average diff% 
for cross-type-down: (-average diff%)






## run strategy

`clj -X:bollinger-strategy`

`clj -X:bollinger-optimizer`


# ma-ma confirmation
- kuerzerer ma (1h = 4* 15 min) cross-up laengere ma. (6h = 24* 15 min) => long


# supertrend
- :up or :down
- regieme: zeitraum durchgehend gleicher wert.
- regieme period dataset.

- number regiemes
- average-bars-regieme-up + maverage-bars-regieme-up
- average next bar return (for up + down)



DANIEL:

- time / memory useage.

- histogram / quartiles.

- rolling correlation every x bars based on the PAST window.
  so say every month calculate the correlations and show them.



FLORIAN TODO:

- print dataset into text (should work with bigger datasets than aggrid)

- https://github.com/scicloj/wadogo  for percentile calculation.

- goldly: create date (js/Date. in repl)

- goldly: arrow endpoint (for bigger datasets) + clojurescript arrow reader.

- goldld: if nrepl .nrepl-port (in main folder)

- https://github.com/johnmn3/injest   BETTER THREADING MACROS


https://github.com/scicloj/scicloj.ml



  (defn ->html [df]
    (apply vector
           :table
           (apply vector :tr (mapv (fn [c] [:th (str c)]) (.ordered-columns df)))
           (mapv
            (fn [row]
              (apply vector :tr (mapv (fn [[_ v]] [:td (pr-str v)]) row)))
            df)))
