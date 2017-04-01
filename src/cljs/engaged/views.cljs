(ns engaged.views
  (:require [re-frame.core :as re-frame]
            [engaged.routes :as routes]
            [engaged.views.navbar :as navbar]))

(defn route->view [route]
  (case route
    :lobby [:div
            [:div "here's the lobby"]
            [:a {:href (routes/about)} "go to about"]]
    :about [:div
            [:div "about stuff..."]
            [:a {:href (routes/lobby)} "go home"]]))

(defn current-route-view []
  (let [route (re-frame/subscribe [:route])]
    [:div
     [navbar/view]
     [:div.container-fluid
      (route->view @route)]]))

(defn loading-view []
  [:div "Loading..."])

(defn resuming-view []
  [:div "Checking authentication..."])

(defn main-app-window []
  (let [state (re-frame/subscribe [:app-state])]
    (case @state
      :loading  [loading-view]
      :resuming [resuming-view]
      :running  [current-route-view])))
