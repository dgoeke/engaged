(ns engaged.views.navbar
  (:require [re-frame.core :refer [subscribe dispatch]]
            [engaged.routes :as routes]))

(defn navbar-base-item [href text]
  [:li.nav-item
   [:a.nav-link {:href href} text]])

(defn navbar-current-item [text]
  [:li.nav-item.active
   [:a.nav-link {:href "#"} text [:span.sr-only "(current)"]]])

(defn navbar-item [new-route href text]
  (let [cur-route @(subscribe [:route])]
    (if (= cur-route new-route)
      (navbar-current-item text)
      (navbar-base-item href text))))

(defn logged-in-dropdown [{:as profile :keys [picture name]}]
  [:ul.navbar-nav.ml-auto
   [:li.nav-item.dropdown
    [:a#dropdown02.nav-link.dropdown-toggle {:href          "#"
                                             :data-toggle   "dropdown"
                                             :aria-haspopup "true"
                                             :aria-expanded "false"}
     [:img.profile-image.rounded-circle {:src picture}]
     name]
    [:div.dropdown-menu {:aria-labelledby "dropdown02"}
     [:a.dropdown-item {:on-click #(dispatch [:logout])} "Sign out"]]]])

(defn signin-button []
  [:span.form-inline
   [:button.btn.btn-success.my-2.my-sm-0 {:on-click #(dispatch [:login])} "Sign in"]])

(defn view []
  (let [{:keys [profile]} @(subscribe [:auth])]
    [:nav.navbar.navbar-toggleable-md.navbar-inverse.bg-inverse.fixed-top.justify-content-end
     [:button.navbar-toggler.navbar-toggler-right {:type          "button"
                                                   :data-toggle   "collapse"
                                                   :data-target   "#navbar"
                                                   :aria-controls "#navbar"
                                                   :aria-expanded "false"
                                                   :aria-label    "Toggle navigation"}
      [:span.navbar-toggler-icon]]

     [:a.navbar-brand {:href "/"} "Engaged"]

     [:div#navbar.collapse.navbar-collapse
      [:ul.navbar-nav.mr-auto
       [navbar-item :about (routes/about) "About"]
       [navbar-item :lobby (routes/lobby) "My Games"]]

      (if profile
        [logged-in-dropdown profile]
        [signin-button])]]))
