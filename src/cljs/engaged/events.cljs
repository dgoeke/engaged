(ns engaged.events
  (:require [re-frame.core :as re-frame :refer [reg-event-db reg-event-fx]]
            [engaged.auth :as auth]
            [engaged.db :as db]
            [reagent.core :as r]))

(reg-event-db :initialize-db
              (constantly db/default-db))

(reg-event-db :set-route
              (fn [db [_ route]]
                (assoc db :route route)))

(reg-event-db :loading-complete
              (fn [db _]
                (when-not (= (:app-state db) :resuming)
                  (auth/maybe-show-login))
                db))

(reg-event-db :login-resuming
              (fn [db _]
                (assoc db :app-state :resuming)))

(reg-event-db :logged-in
              (fn [db [_ auth]]
                (-> db
                    (assoc :auth auth)
                    (assoc :app-state :running))))
