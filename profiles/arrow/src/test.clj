(ns test
  (:require
   [tablecloth.api :as tc]
   [tech.v3.io :as techio]
   [tech.v3.libs.arrow :as arrow]))

(-> {:x [1 2 3]
     :y ["A" "B" "A"]}
    tc/dataset
    (arrow/write-dataset-to-stream! "/tmp/data.arrow" {}))
