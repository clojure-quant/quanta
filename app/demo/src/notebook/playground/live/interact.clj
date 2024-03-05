(ns notebook.playground.live.interact
  (:require [ta.interact.subscription :as sub]))



(sub/subscribe-live :crypto-watch)


(-> @sub/results-a :crypto-watch)

(-> @sub/visualizations-a :crypto-watch)

