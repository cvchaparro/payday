(ns io.cvcf.payday.web.views.deals
  (:require
   [clojure.string :as s]

   [io.cvcf.payday.web.controllers.deals :as deals]
   [io.cvcf.payday.web.htmx :refer [page-htmx page]]
   [io.cvcf.payday.web.views.components :as c]

   ;; Helpers
   [io.cvcf.payday.helpers :as h]))

(defn down-payment [selected]
  (let [selected (keyword selected)
        minimum (get-in deals/deal-types [selected :min-down])
        maximum (get-in deals/deal-types [selected :total])]
    (c/input "down-payment"
             :type "number"
             :placeholder "e.g. 1234.56"
             :extra {:step "0.01"
                     :min minimum
                     :max maximum})))

(defn new-deal [& {:keys [target-id endpoint]}]
  [:div.column.is-two-fifths
   [:h3.title.is-3 "New deal"]
   [:div.box
    [:form {:hx-post endpoint :hx-target (c/id target-id) :hx-swap "outerHTML"}
     (c/field "Date*" (c/input "date" :type "date" :placeholder nil))

     (c/field-group "Guest Information"
       (c/field "Full Name*" (c/input "name" :placeholder "e.g. John Smith")))

     (c/field-group "Contact Information"
       (c/field "Email*" (c/input "primary-email" :type "email" :placeholder "e.g. john.smith@email.com"))
       (c/field "Secondary Email" (c/input "secondary-email" :type "email" :placeholder "e.g. john.smith2@email.com" :required? false))
       (c/field "Phone*" (c/input "primary-phone" :type "phone" :placeholder "e.g. 123.456.7890"))
       (c/field "Secondary Phone" (c/input "secondary-phone" :type "phone" :placeholder "e.g. 123.567.8900" :required? false))

       (c/field-group "Address"
         (c/field "Street*"   (c/input "street" :placeholder "e.g. 123 Main St"))
         (c/field "City*"     (c/input "city" :placeholder "e.g. Anycity"))
         (c/field "State*"    (c/input "state" :placeholder "e.g. Anystate"))
         (c/field "Zip code*" (c/input "zip-code" :placeholder "e.g. 12345"))
         (c/field "Country*"  (c/input "country" :placeholder "e.g. USA"))))

     (c/field-group "Tour Information"
       (c/field "Tour type*"     (c/input "tour-type" :placeholder "e.g. Monster"))
       (c/field "Frontline rep*" (c/input "frontline-rep" :placeholder "e.g. Jon Bon Jovi")))

     (c/field-group "Member Information"
       (c/field "Contract Number*" (c/input "contract" :placeholder "e.g. 00065..."))
       (c/field "Member Number*"   (c/input "member" :placeholder "e.g. 00203...")))

     (c/field-group "Deal Information"
       (let [deal-names (keys deals/deal-types)
             values     (map name deal-names)
             selected   (format "%dk" (apply max (map deals/disco-package->num deal-names)))]
         [:div
          (c/field "Deal type*"
            (c/select "Deal Type" values
                      :target-id "down-payment"
                      :endpoint  "/deals/down-payment"
                      :selected  selected))

          (c/field "Down payment*" (down-payment selected))])

       (c/field "TO"
         (c/input "to" :placeholder "e.g. Cameron Diaz" :required? false))

       (c/checkbox "Split?")

       [:div.buttons.is-centered
        (c/button "Add" :type "submit" :classes ["is-primary" "is-medium"])])]]])

(defn show-deal [deal]
  (let [{:keys [year month day members
                primaryemail secondaryemail primaryphone secondaryphone
                street city state zip country
                tourtype frontlinerep
                contractnum membernum
                dealtype downpayment turnover split]}
        deal]
    [:li.my-4
     [:div.card
      [:header.card-header
       [:div.card-header-title.is-centered
        [:h4.py-2.my-2 (interpose [:br] (map s/trim (s/split members #",")))]]]
      [:hr.my-0]
      [:div.card-content
       [:div.columns.is-centered
        [:div.column
         [:div [:strong "Email: "] [:a {:href (format "mailto:%s" primaryemail)} primaryemail]]
         (when secondaryemail
           [:div [:strong "Secondary Email: "] [:a {:href (format "mailto:%s" secondaryemail)} secondaryemail]])
         [:div [:strong "Phone: "] [:a {:href (format "tel:%s" primaryphone)} primaryphone]]
         (when secondaryphone
           [:div [:strong "Secondary Phone​: "] [:a {:href (format "tel:%s" secondaryphone)} secondaryphone]])
         [:div [:strong "Address:"]
          [:address
           street [:br]
           city ", " state " " zip [:br]
           country]]]
        [:div.column
         [:div [:strong "Tour Type: "] tourtype]
         [:div [:strong "Frontline Rep: "] frontlinerep]

         [:div [:strong "Deal Type: "] dealtype]
         [:div [:strong "Down Payment: "] "$" (format "%.2f" downpayment)]
         (when (and turnover (seq turnover))
           [:div [:strong "Turn Over: "] turnover])
         [:div [:strong "Split?: "] (if (and split (not (zero? split))) "Yes" "No")]]]]
      [:div.card-footer
       [:div.card-footer-item [:div.is-small (s/join "-" (map #(format "%02d" %) [year month day]))]]
       [:div.card-footer-item [:strong "Contract #: "] contractnum]
       [:div.card-footer-item [:strong "Member #: "] membernum]]]]))

(defn all-deals [& {:keys [db eid]}]
  [:div.column.is-two-fifths {:id eid}
   [:h3.title.is-3 "Deals"]
   [:div.content
    (vec (concat [:ol] (mapv show-deal (deals/get-deals db))))]])

(defn create-deal! [& {:keys [db params eid]}]
  (deals/save-deal! db params)
  (page (all-deals :db db :eid eid)))

(defn deals-routes [{:keys [db]}]
  (let [deals-list-id "deals-list"]
    [["" {:get (fn [_]
                 (page-htmx [:div.columns.is-centered
                             (new-deal :target-id deals-list-id :endpoint "/deals/add")
                             (all-deals :db db :eid deals-list-id)]))}]
     ["/new" {:get (fn [_]
                     (page (new-deal :target-id deals-list-id :endpoint "/deals/add")))}]
     ["/add" {:post (fn [{:keys [params]}]
                      (create-deal! :db db :params params :eid deals-list-id))}]
     ["/all" {:get (fn [_]
                     (page-htmx [:div.columns.is-centered
                                 (all-deals :db db :eid deals-list-id)]))}]
     ["/get" {:post (fn [{:keys [params]}]
                      (let [{:keys [year month day deal-id]} params
                            deals (deals/get-deals db :year year :month month :day day :id deal-id)]
                        (page [:div.content (vec (concat [:ol] (mapv show-deal deals)))])))}]
     ["/down-payment" {:get (fn [{:keys [params]}]
                              (page (down-payment (:deal-type (h/->map params)))))}]]))
