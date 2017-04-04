(ns engaged.views.lobby
  (:require [re-frame.core :refer [dispatch subscribe]]
            [engaged.routes :as routes]))

(defn game-list-card [games]
  [:div.card
   [:div.card-block
    [:h3.card-title "Game List"]
    (if (pos? (count games))
      [:p "Choose one of the games you've made previously."]
      [:p "You haven't created any games yet. Let's do that now!"])]
   [:ul.list-group.list-group-flush
    (for [game games]
      [:a.list-group-item.list-group-item-action {:key  (:_id game)
                                                  :href (routes/game {:id (:_id game)})}
       (:game-name game)])]])

(defn create-game-card []
  (let [game-name (atom "")]
    (fn []
      [:div.card
       [:div.card-block
        [:h3.card-title "Create a Game"]
        [:div.form-group.row
         [:div.col-9
          [:input#newGameName.form-control {:type        "text"
                                            :placeholder "The Dream-Forests of Drusilia"
                                            :on-change   #(reset! game-name (-> % .-target .-value))}]]
         [:div.col-3
          [:button.btn.btn-primary {:type     "submit"
                                    :on-click #(dispatch [:create-game {:type :13th-age :name @game-name}])} "Create!"]]]]])))

(defn view []
  (let [games @(subscribe [:game-list])]
    [:div.row.justify-content-md-center
     [:div.col-md-5
      [game-list-card games]]
     [:div.col-md-5
      [create-game-card]]]))
