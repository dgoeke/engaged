(ns engaged.styles
  (:require [garden.def :refer [defrule defstyles]]
            [garden.stylesheet :refer [rule]]))

(defstyles screen
  [:.main-container {:padding-top "5rem"}]

  [:img.profile-image {:position      "relative"
                       :top           "-5px"
                       :float         "left"
                       :left          "-5px"
                       :height        "32px"
                       :padding-right "10px"}]

  [:a.dropdown-item {:cursor "pointer"}])
