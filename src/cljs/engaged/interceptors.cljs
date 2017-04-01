(ns engaged.interceptors
  (:require [cljs.spec :as s]
            [engaged.specs :as specs]
            [engaged.config :refer [config]]
            [re-frame.core :as re-frame]))

(defn check-and-throw
  "throw an exception if db doesn't match the spec"
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "spec check failed: "
                         (s/explain-str a-spec db)) {}))))

(def check-spec-interceptor
  (re-frame/after (partial check-and-throw ::specs/db)))

(def standard-interceptors
  [(when (:debug? config) check-spec-interceptor)
   re-frame/trim-v])

(defn reg-event-db
  ([id handler-fn]
   (reg-event-db id nil handler-fn))
  ([id interceptors handler-fn]
   (re-frame.core/reg-event-db
    id
    (into standard-interceptors interceptors)
    handler-fn)))
