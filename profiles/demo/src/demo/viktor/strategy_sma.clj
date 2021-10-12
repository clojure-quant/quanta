(ns demo.viktor.strategy-sma
  (:require
   [taoensso.timbre :refer [trace debug info error]]
   [tick.alpha.api :as t]
   [tech.v3.dataset.print :as print]
   [tech.v3.dataset :as tds]
   [tech.v3.datatype.datetime :as datetime]
   [tech.v3.datatype.functional :as fun]
   [tablecloth.api :as tablecloth]
   [ta.dataset.helper :as helper]
   [ta.series.indicator :as ind]
   [ta.warehouse :as wh]
   [ta.dataset.backtest :as backtest]
   [ta.dataset.bollinger :as bollinger]
   [demo.env.warehouse :refer [w]]
   [demo.studies.helper.experiments-helpers :as experiments-helpers]))

