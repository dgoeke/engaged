(ns engaged.app
  (:require [cljsjs.bootstrap]
            [cljsjs.jquery]
            [devtools.core :as devtools]
            [engaged.config :refer [config]]
            [engaged.events]
            [engaged.routes]
            [engaged.subs]
            [engaged.views :as views]
            [mount.core :as mount :refer [defstate]]
            [re-frame.core :as re-frame]
            [re-frisk.core :refer [enable-re-frisk!]]
            [reagent.core :as reagent :refer [atom]]))

(defn dev-setup []
  (when (:debug? config)
    (enable-console-print!)
    (enable-re-frisk!)
    (println "dev mode")))

(defn mount-root-element []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-app-window]
                  (.getElementById js/document "container")))

(defstate root-element
  :start (mount-root-element))

(defn ^:export init []
  (dev-setup)
  (re-frame/dispatch-sync [:initialize-db])
  (mount/start)
  (re-frame/dispatch-sync [:loading-complete]))
