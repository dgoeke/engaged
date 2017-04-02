(ns engaged.views.lobby
  (:require [re-frame.core :refer [dispatch subscribe]]
            [engaged.routes :as routes]))

(defn view []
  [:div
   [:h4 "Game List"]
   [:div.row>div.col-md-4
    [:ul.list-group
     (for [game @(subscribe [:game-list])]
       [:li.list-group-item {:key (:_id game)} (:game-name game)])]]
   [:p [:a {:href (routes/about)} "go to about"]]])
