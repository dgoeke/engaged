(ns engaged.specs
  (:require [cljs.spec :as s]))


(def default-db
  {:route     :lobby
   :app-state :loading
   :auth      nil})

(s/def ::route (constantly true))

(s/def ::app-state #{:loading :resuming :running})

(s/def ::auth (constantly true))

(s/def ::db (s/keys :req-un [::route
                             ::app-state
                             ::auth]))
