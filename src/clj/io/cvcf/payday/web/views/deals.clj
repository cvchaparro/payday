(ns io.cvcf.payday.web.views.deals
  (:require
   [io.cvcf.payday.helpers :as h]
   [io.cvcf.payday.web.controllers.deals :as deals]
   [io.cvcf.payday.web.views.components :as c]
   [io.cvcf.payday.web.htmx :refer [page-htmx ui]]
   [simpleui.core :as simpleui :refer [defcomponent]]))

(defcomponent down-payment [req selected]
  (let [selected (keyword selected)
        minimum (get-in deals/deal-types [selected :min-down])
        maximum (get-in deals/deal-types [selected :total])]
    (c/input "down-payment"
             :type "number"
             :placeholder "e.g. 1234.56"
             :extra {:step "0.01"
                     :min minimum
                     :max maximum})))

(defcomponent new-deal [req target-id endpoint]
  [:div.column.is-two-fifths
   [:h3.title.is-3 "New deal"]
   [:form {:id id :hx-post endpoint :hx-target (c/id target-id) :hx-swap "outerHTML"}
    (c/field "Date"
      (c/input "date" :type "date" :placeholder nil))

    (c/field-group "Guest Information"
      (c/field "Full Name" (c/input "name" :placeholder "e.g. John Smith")))

    (c/field-group "Contact Information"
      (c/field "Email" (c/input "email" :type "email" :placeholder "e.g. john.smith@email.com"))
      (c/field "Phone" (c/input "phone" :type "phone" :placeholder "e.g. 123.456.7890")))

    (c/field-group "Address"
      (c/field "Street"   (c/input "street" :placeholder "e.g. 123 Main St"))
      (c/field "City"     (c/input "city" :placeholder "e.g. Anycity"))
      (c/field "State"    (c/input "state" :placeholder "e.g. Anystate"))
      (c/field "Zip code" (c/input "zip-code" :type "number" :placeholder "e.g. 12345"))
      (c/field "Country"  (c/input "country" :placeholder "e.g. USA")))

    (c/field-group "Tour Information"
      (c/field "Tour type"     (c/input "tour-type" :placeholder "e.g. Monster"))
      (c/field "Frontline rep" (c/input "frontline-rep" :placeholder "e.g. Jon Bon Jovi")))

    (c/field-group "Member Information"
      (c/field "Contract #" (c/input "contract" :placeholder "e.g. 00065..."))
      (c/field "Member #"   (c/input "member" :placeholder "e.g. 00203...")))

    (let [deal-names (keys deals/deal-types)
          values (map name deal-names)
          selected (format "%dk" (apply max (map deals/disco-package->num deal-names)))]
      [:div
       (c/field "Deal type"
         (c/select "Deal Type" values
                   :target-id "down-payment"
                   :endpoint  "/deals/down-payment"
                   :selected  selected))

       (c/field "Down payment" (down-payment nil selected))])

    (c/field "TO"
      (c/input "to" :placeholder "e.g. Cameron Diaz" :required? false))

    (c/checkbox "Split?")

    [:div.buttons.is-centered
     (c/button "Add" :type "submit" :classes ["is-primary" "is-medium"])]]])

(defcomponent all-deals [req & {:keys [eid]}]
  [:div.column.is-two-fifths {:id eid}
   [:h3.title.is-3 "Deals"]
   (map #(vec [:p (get req %1)])
        [;; Guest information
         :date :name :email :phone :street :city :state :zip-code :country
         ;; Tour information
         :tour-type :frontline-rep
         ;; Deal information
         :contract :member :deal-type :down-payment :to])])

(defn deals-routes []
  (let [deals-list-id "deals-list"]
    [["" {:get (fn [req]
                 (page-htmx [:div.columns.is-centered
                             (new-deal req deals-list-id "/deals/all")
                             (all-deals req :eid deals-list-id)]))}]
     ["/new" {:get (fn [req]
                     (ui (new-deal req deals-list-id "/deals/all")))}]
     ["/all" {:post (fn [{:keys [form-params]}]
                      (ui (all-deals (h/->map form-params) :eid deals-list-id)))}]
     ["/down-payment" {:get (fn [{:keys [params]}]
                              (ui (down-payment nil (:deal-type (h/->map params)))))}]]))
