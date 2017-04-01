(ns engaged.events
  (:require [re-frame.core :as re-frame :refer [reg-event-fx reg-fx]]
            [accountant.core :as accountant]
            [engaged.auth :as auth]
            [engaged.db :as db]
            [engaged.interceptors :refer [standard-interceptors reg-event-db]]))

(reg-event-db :initialize-db
              (constantly db/default-db))

(reg-event-db :set-route
              (fn [db [route]]
                (assoc db :route route)))

(reg-event-fx :loading-complete
              [re-frame/debug]
              (fn [{:keys [db]} _]
                (merge {}
                       {:db (assoc db :app-state :running)}
                       (when-not (= (:app-state db) :resuming)
                         {:auth :check-creds}))))

(reg-event-db :login-resuming
              (fn [db _]
                (assoc db :app-state :resuming)))

(reg-event-db :set-auth
              (fn [db [auth]]
                (assoc db :auth auth)))

(reg-event-fx :logout
              (constantly {:auth     :logout
                           :dispatch [:set-auth nil]}))

(reg-event-fx :login
              (constantly {:auth :login}))

(reg-fx :auth
        (fn [val]
          (case val
            :login       (auth/login!)
            :logout      (auth/logout!)
            :check-creds (auth/check-stored-credentials!)

            (throw (ex-info "Unknown auth command" {:command val})))))
