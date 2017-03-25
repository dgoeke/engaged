(ns engaged.views
  (:require [re-frame.core :as re-frame]
            [engaged.routes :as routes]))

(defn route->view [route]
  (case route
    :lobby [:div
            [:div "here's the lobby"]
            [:a {:href (routes/about-route)} "go to about"]
            [:button {:on-click #(re-frame/dispatch [:logout])} "log out"]]
    :about [:div
            [:div "about stuff..."]
            [:a {:href (routes/lobby-route)} "go home"]]))

(defn current-route-view []
  (let [route (re-frame/subscribe [:route])]
    [:div
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
