(ns io.cvcf.payday.web.views.deals
  (:require
   [io.cvcf.payday.web.views.components :as c]
   [io.cvcf.payday.web.htmx :refer [page-htmx]]
   [simpleui.core :as simpleui :refer [defcomponent]]))

(defcomponent ^:endpoint new-deal [req target-id]
  [:div.column
   [:h3.title.is-3 "New deal"]
   [:form {:id id :hx-post (str "/" target-id)}
    [:div.field.is-grouped.is-horizontal
     (c/select-box "Year" [2024])
     (c/select-box "Month" (range 1 13))
     (c/select-box "Day" [])]
    [:div.control
     [:input#add-deal.button.is-primary
      {:type "submit" :value "Add" :hx-target (str "#" target-id)}]]]])

(defcomponent ^:endpoint deal-list [req elt-id]
  [:div.column
   [:h3.title.is-3 "Deals"]
   [:div {:id elt-id}]])

(defn deals-routes [base-path]
  (simpleui/make-routes
   base-path
   (fn [req]
     (let [deal-list-id "deal-list"]
       (page-htmx [:div.columns
                   (new-deal req deal-list-id)
                   (deal-list req deal-list-id)])))))
