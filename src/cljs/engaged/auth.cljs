(ns engaged.auth
  (:require [alandipert.storage-atom :refer [local-storage clear-local-storage!]]
            [cljsjs.auth0-lock]
            [engaged.config :refer [config]]
            [mount.core :refer [defstate]]
            [re-frame.core :as re-frame]))

(defstate stored-auth
  :start (local-storage (atom {}) :auth))

(def auth0-params
  {:auth {:responseType "token"
          :params       {:scope "openid roles"}}})

(defstate ^{:on-reload :noop} lock
  :start (js/Auth0Lock. (-> config :auth0 :client-id)
                        (-> config :auth0 :domain)
                        (clj->js auth0-params)))

(defn on-logged-in [auth profile]
  (swap! @stored-auth assoc :auth auth :profile profile)
  (let [{:keys [idToken accessToken]} auth]
    (re-frame/dispatch [:set-auth {:id-token     idToken
                                   :access-token accessToken
                                   :profile      profile}])))

(defn ->clj [obj]
  (js->clj obj :keywordize-keys true))

(defn on-auth-result [js-auth]
  (when-let [auth-result (->clj js-auth)]
    (.getUserInfo @lock (:accessToken auth-result)
                  (fn [error profile]
                    (if error
                      (throw (ex-info "Error getting profile." {:error error}))
                      (on-logged-in auth-result
                                    (->clj profile)))))))

(defstate ^{:on-reload :noop} lock-on-auth
  :start (.on @lock "authenticated" on-auth-result))

(defn check-stored-credentials! []
  (let [{:keys [auth profile]} @@stored-auth]
    (when (and auth profile)
      (on-logged-in auth profile))))

(defn login! []
  (when-not (check-stored-credentials!)
    (.show @lock (clj->js auth0-params))))

(defn logout! []
  (clear-local-storage!))
