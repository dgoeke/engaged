(ns engaged.events
  (:require [re-frame.core :as re-frame :refer [reg-event-fx reg-fx]]
            [accountant.core :as accountant]
            [engaged.auth :as auth]
            [engaged.db :as db]
            [engaged.interceptors :refer [standard-interceptors reg-event-db]]
            [engaged.routes :as routes]))

(reg-event-fx :initialize-db
              [re-frame/debug]
              (constantly {:db      db/default-db
                           :pouchdb [:all-games]}))

(reg-event-fx :db-changed
              (fn [db [changes]]
                {:pouchdb [:all-games]}))

(reg-event-db :game-list
              (fn [db [games]]
                (assoc db :games (->> (:rows games)
                                      (map :doc)))))

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

(reg-event-fx :create-game
              (fn [_ [_ args]]
                {:pouchdb [:create-game args]}))

(reg-event-fx :game-created
              (fn [_ [_ id]]
                {:navigate (routes/game {:id id})
                 :pouchdb  [:all-games]}))

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

(reg-fx :navigate
        (fn [val]
          (accountant/navigate! val)))

(reg-fx :auth
        (fn [val]
          (case val
            :login       (auth/login!)
            :logout      (auth/logout!)
            :check-creds (auth/check-stored-credentials!)

            (throw (ex-info "Unknown auth command" {:command val})))))

(reg-fx :pouchdb
        (fn [[cmd args]]
          (case cmd
            :all-games   (db/all-games!)
            :create-game (db/create-game! args)

            (throw (ex-info "Unknown pouchdb command" {:command val})))))
