(ns engaged.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame :refer [reg-sub]]))

(reg-sub :ui-state
         (fn [db _]
           (:ui-state db)))

(reg-sub :route
         (fn [db _]
           (:route db)))

(reg-sub :auth
         (fn [db _]
           (:auth db)))

(reg-sub :app-state
         (fn [db _]
           (:app-state db)))
