(ns engaged.views
  (:require [re-frame.core :as re-frame]))

(defn route->view [route]
  (case route
    [:div (str "Showing you the page " route)]))

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
