(ns ta.import.provider.kibot-http.demo
  (:require
   [aleph.http :as http]
   [manifold.deferred :as d]
   [clj-commons.byte-streams :as bs]))

(defn extract-asset [header]
  (let [m (re-matches #".*filename=(.*)\.txt" header)
        [_ asset] m]
    asset))

(defn get-asset [request]
  (-> request
      :headers
      (get "content-disposition")
      extract-asset))

(defn download-link [url]
  (let [request @(http/get url)
        asset (get-asset request)]
    {:asset asset
     :data (bs/to-string (:body request))}))


(comment
  (def url "http://api.kibot.com?action=download&link=fifzfgf9fjf8snf5f9fbfgfjfa1nnkfg1nfvf7snf4fjfaf71mnkf2f8fifrf3f2fbfgf7frsnn6nkfb1nfhfkfjf6snfmsusvsbslsknkf9f8fgf7faftfif6snnhnkfbfgfifafgfrfifgf7snnhncnhncnhn2n2n6nkfrf9faf7fzfgsnnhnkfifgfgfifzf5fhf7f8fgsnnhnkfaf7fdf2f6fifafbf7fbfbf9fjf8snn6nkf2fbf7fasnf5fjf7fafgf6f7f5f8f7fas1fdfhfif9f6n3fzfjfhnkfvfifbfbfefjfafrsnn8ngn8fhn8f4f5fdf51za4afaraeas7n72")
  url
  (def header  "attachment; filename=ZARUSD.txt")
  (extract-asset header)

  (-> @(http/get url)
      get-asset)

  @(http/head url)

  (download-link url)
  
  (-> @(http/get url)
      :body
      (bs/to-string)
      prn)

 ; 
  )



  
  
