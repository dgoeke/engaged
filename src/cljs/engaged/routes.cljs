(ns engaged.routes
  (:require-macros [secretary.core :refer [defroute]])
  (:import goog.History)
  (:require [clojure.string :as str]
            [accountant.core :as accountant]
            [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [mount.core :refer [defstate]]
            [re-frame.core :as re-frame]))

(defn dispatch! [path]
  (if-not (str/includes? path "id_token")
    (secretary/dispatch! path)
    (re-frame/dispatch-sync [:login-resuming])))

(defroute lobby "/" []
  (re-frame/dispatch [:set-route :lobby]))

(defroute about "/about" []
  (re-frame/dispatch [:set-route :about]))

(accountant/configure-navigation! {:nav-handler  dispatch!
                                   :path-exists? secretary/locate-route})

(defstate initial-dispatch
  :start (accountant/dispatch-current!))
