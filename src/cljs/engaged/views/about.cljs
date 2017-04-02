(ns engaged.views.about
  (:require [re-frame.core :refer [dispatch subscribe]]
            [engaged.routes :as routes]))

(defn view []
  [:div
   [:div "about stuff..."]
   [:a {:href (routes/lobby)} "go home"]])
