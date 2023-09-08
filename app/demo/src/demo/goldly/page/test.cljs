(ns demo.goldly.page.test
  (:require
   [reagent.core :as r]
   [goldly.page :as page]
   [demo.goldly.view.tsymbol :refer [symbol-picker]]
   [joseph.upload :refer [upload-file-ui]]
   ))

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

(defn test-page [{:keys [_route-params _query-params _handler] :as _route}]
 [:div.bg-blue-300.m-5
 [:p.text-blue.text-xl.bg-yellow-300 "ui component tests"]
   [:p "Aries: " (zodiac-symbol :cancer)]
   [:div.w-64
    [symbol-picker symbol-atom [:symbol]]
    [upload-file-ui]
    ]])




(page/add test-page :user/test)
