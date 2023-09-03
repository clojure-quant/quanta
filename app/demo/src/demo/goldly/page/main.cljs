(ns demo.goldly.page.main
  (:require
   [reagent.core :as r]
   [goldly.page :as page]
   [demo.goldly.lib.ui :refer [link-dispatch link-href]]
   [demo.goldly.view.tsymbol :refer [symbol-picker]]))

; main page 

(def zodiac-symbols
  {:aries  '\u2648 ; ♈︎ Aries (Ram)	
   :taurus '\u2649 ; ♉︎ Taurus (Bull)	U+2649
   :gemini '\u264a ; ♊︎ Gemini (Twins)	U+264A
   :cancer '\u264b  ;♋︎	Cancer (Crab)	U+264B
   :leo '\u264c ;♌︎	Leo (Lion)	U+264C
   :virgio '\u264d ;♍︎	Virgo (Virgin)	U+264D
   :libra '\u264e ;♎︎	Libra (Scale)	U+264E
   :scorpio '\u264f ;♏︎	Scorpio (Scorpion)	U+264F
   :sagittarius '\u2650  ;♐︎	Sagittarius (Archer)	U+2650
   :capricorn '\u2651 ;♑︎	Capricorn (Sea-Goat)	U+2651
   :aquarius '\u2652 ;♒︎	Aquarius (Waterbearer)	U+2652
   :pisces '\u2653 ;♓︎	Pisces (Fish)	U+2653
   })

(defn zodiac-symbol [s]
  (s zodiac-symbols))

(defonce symbol-atom (r/atom {:symbol ""}))

(defn main-page [{:keys [_route-params _query-params _handler] :as _route}]
  [:div

   [:p "Aries: " (zodiac-symbol :cancer)]

   ; trateg web ui
   [:div.bg-blue-300.m-5
    [:h1.text-xl.text-red-600 "trateg "]

    [:p.text-blue.text-xl "tradingview"]
    [link-dispatch [:bidi/goto :algo/tv] "tradingview-algo"]
    [link-dispatch [:bidi/goto :tradingview] "tradingview-udf"]

    [:p.text-blue.text-xl "backtest"]
    [link-href "/algo/backtest" "backtester"]

    [:p.text-blue.text-xl "warehouse"]
    [link-href "/warehouse" "warehouse"]
    [link-href "/series" "series"]]
    
   ; trateg demos
   [:div.bg-blue-300.m-5
    [:h1.text-xl.text-red-600 "gann tools"]
    [link-href "/gann" "gann chart"]
    [link-href "/joseph" "joseph"]]

   [:div.bg-blue-300.m-5
    [:p.text-blue.text-xl.bg-yellow-300 "ui component tests"]
    [:div.w-64
     [symbol-picker symbol-atom [:symbol]]]]

   ; goldly developer tools
   [:div.bg-blue-300.m-5
    [:p.text-blue.text-xl "goldly developer tools"]
    [link-dispatch [:bidi/goto :viewer :query-params {}] "notebook viewer"]
    [link-dispatch [:bidi/goto :scratchpad] "scratchpad"]
    [link-dispatch [:bidi/goto :environment] "environment"]
    [link-dispatch [:bidi/goto :devtools] "devtools help"]]

;
   ])

(page/add main-page :user/main)
