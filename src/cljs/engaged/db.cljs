(ns engaged.db
  (:require [cljsjs.pouchdb]))

(def default-db
  {:route     :lobby
   :app-state :loading
   :auth      nil})
