(ns juan.download.fxcm
  (:require
   [clj-http.client :as http]
   [clojure.xml :as xml]))

;; download

(def fxcm-url "https://rates.fxcm.com/RatesXML3")

(defn fxcm-download []
  (-> (http/get fxcm-url {:accept :html})
      (:body)))

;; parsing

(defn string->stream
  ([s] (string->stream s "UTF-8"))
  ([s encoding]
   (-> s
       (.getBytes encoding)
       (java.io.ByteArrayInputStream.))))

(defn parse-tag [{:keys [tag _attrs content]}]
  [tag (first content)])

(defn parse-rate [r]
  (let [content (:content r)]
    (->> (map parse-tag content)
         (into {}))))

(defn parse-rates [seq]
  (map parse-rate seq))

(defn sanitize [{:keys [Bid Ask Low High PreviousClose] :as rate}]
  (let [bid (Float/parseFloat Bid)
        ask (Float/parseFloat Ask)
        low (Float/parseFloat Low)
        high (Float/parseFloat High)
        prev (Float/parseFloat PreviousClose)]
    (assoc rate 
           :Bid bid 
           :Ask ask 
           :Low low 
           :High high 
           :PreviousClose prev)))

(defn fxcm-parse [xml-data]
   (->> xml-data
      string->stream
      xml/parse
      xml-seq
      first
      :content
      parse-rates
      (map sanitize) 
       ))

(comment
  (def data  (fxcm-download))
  data
  (string->stream "hello")

  (fxcm-parse data)


  (into {} [[:tag 3] [:value 5]])

  [:tag :Rate,
   :attrs nil,
   :content
   [{:tag :Symbol, :attrs nil, :content ["EURUSD"]}
    {:tag :Bid, :attrs nil, :content ["1.0906"]}
    {:tag :Ask, :attrs nil, :content ["1.09067"]}
    {:tag :High, :attrs nil, :content ["1.09308"]}
    {:tag :Low, :attrs nil, :content ["1.08818"]}
    {:tag :Direction, :attrs nil, :content ["-1"]}
    {:tag :Last, :attrs nil, :content ["15:38:30"]}
    {:tag :Date, :attrs nil, :content ["2023-11-23"]}
    {:tag :Time, :attrs nil, :content ["2023-11-23 15:38:30"]}
    {:tag :PreviousClose, :attrs nil, :content ["1.08872"]}
    {:tag :PreviousDate, :attrs nil, :content ["2023-11-21 17:00:00"]}]]

;
  ) 
