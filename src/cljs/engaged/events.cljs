(ns engaged.events
  (:require [re-frame.core :as re-frame :refer [reg-event-db reg-event-fx reg-fx]]
            [accountant.core :as accountant]
            [engaged.auth :as auth]
            [engaged.db :as db]
            [reagent.core :as r]))

(reg-event-db :initialize-db
              (constantly db/default-db))

(reg-event-db :set-route
              (fn [db [_ route]]
                (assoc db :route route)))

(reg-event-fx :loading-complete
              (fn [{:keys [db]} _]
                (when-not (= (:app-state db) :resuming)
                  {:auth :maybe-show-login})))

(reg-event-db :login-resuming
              (fn [db _]
                (assoc db :app-state :resuming)))

(reg-event-db :logged-in
              (fn [db [_ auth]]
                (-> db
                    (assoc :auth auth)
                    (assoc :app-state :running))))

(reg-event-fx :logout
              (constantly {:auth :logout}))

(reg-fx :auth
        (fn [val]
          (case val
            :maybe-show-login (auth/maybe-show-login!)
            :logout           (do (auth/logout!)
                                  (accountant/navigate! "/")
                                  (.reload js/location))

            (println "Error: unknown auth command:" val))))
