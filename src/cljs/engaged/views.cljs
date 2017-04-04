(ns engaged.views
  (:require [cljs.core.match :refer-macros [match]]
            [re-frame.core :as re-frame]
            [engaged.routes :as routes]
            [engaged.views.navbar :as navbar]
            [engaged.views.lobby :as lobby]
            [engaged.views.about :as about]
            [engaged.views.game :as game]))

(defn route->view [route]
  (match route
         :lobby     [lobby/view]
         :about     [about/view]
         [:game id] [game/view id]

         :else (throw (ex-info "Unknown view requested" {:route route}))))

(defn current-route-view []
  (let [route (re-frame/subscribe [:route])]
    [:div
     [navbar/view]
     [:div.container-fluid.main-container
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
