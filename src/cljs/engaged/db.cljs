(ns engaged.db
  (:require [cljsjs.pouchdb]
            [mount.core :refer [defstate]]
            [cljs-time.core :as t]
            [cljs-time.coerce :as coerce]
            [re-frame.core :refer [dispatch]]))

(def db-name "engaged")

(def default-db
  {:route     :lobby
   :app-state :loading
   :auth      nil})

(defn ->clj [x] (js->clj x :keywordize-keys true))

(def games-index
  {:_id   "_design/games-index"
   :views {:games-index {:map (str (fn [doc]
                                     (when-let [name (aget doc "game-name")]
                                       (js/emit (aget doc "_id")))))}}})

(defstate db
  :start (let [mydb (js/PouchDB. db-name)]
           (.put mydb (clj->js games-index))
           mydb))

(defstate changes
  :start (-> @db
             (.changes (clj->js {:since 0 :include_docs true}))
             (.then #(dispatch [:db-changed (->clj %)]))))

(defn insert! [item]
  (let [new-item (if (:_id item)
                   item
                   (assoc item :_id (-> (t/now) coerce/to-long str)))]
    (.put @db (clj->js new-item))))

(defn all-games! []
  (-> @db
      (.query "games-index" (clj->js {:include_docs true}))
      (.then #(dispatch [:game-list (->clj %)]))))

(defn create-game!
  [{:as info :keys [name type]}]
  (-> (insert! {:game-name name :game-type type})
      (.then (fn [result-js]
               (let [result (->clj result-js)]
                 (dispatch [:game-created (:id result)]))))))
