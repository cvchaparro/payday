(ns io.cvcf.payday.web.views.deals
  (:require
   [clojure.string :as s]

   [io.cvcf.payday.helpers :as h]
   [io.cvcf.payday.web.controllers.deals :as deals]
   [io.cvcf.payday.web.views.components :as c]
   [io.cvcf.payday.web.htmx :refer [page-htmx ui]]))

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
       (c/field "Email*" (c/input "email" :type "email" :placeholder "e.g. john.smith@email.com"))
       (c/field "Phone*" (c/input "phone" :type "phone" :placeholder "e.g. 123.456.7890"))

       (c/field-group "Address"
         (c/field "Street*"   (c/input "street" :placeholder "e.g. 123 Main St"))
         (c/field "City*"     (c/input "city" :placeholder "e.g. Anycity"))
         (c/field "State*"    (c/input "state" :placeholder "e.g. Anystate"))
         (c/field "Zip code*" (c/input "zip-code" :type "number" :placeholder "e.g. 12345"))
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

(defn all-deals [& {:keys [query-fn eid]}]
  [:div.column.is-two-fifths {:id eid}
   [:h3.title.is-3 "Deals"]
   [:div.content
    (vec
     (concat [:ol]
             (mapv (fn [deal]
                     (let [{:keys [date members email phone second_phone
                                   addr_street addr_city addr_state addr_zip addr_country
                                   tour_type frontline_rep
                                   contract_num member_num deal_type down_payment turn_over split]}
                           deal]
                       [:li.my-4
                        [:div.card
                         [:header.card-header
                          [:div.card-header-title.is-centered
                           [:h4.py-2.my-2 (interpose [:br] (map s/trim (s/split members #",")))]]]
                         [:hr.my-0]
                         [:div.card-content
                          [:div [:strong "Email: "] [:a {:href (format "mailto:%s" email)} email]]
                          [:div [:strong "Phone: "] [:a {:href (format "tel:%s" phone)} phone]]
                          (when second_phone
                            [:div [:strong "Secondary Phone​: "] [:a {:href (format "tel:%s" second_phone)} second_phone]])
                          [:div [:strong "Address:"]
                           [:address
                            addr_street [:br]
                            addr_city ", " addr_state " " addr_zip [:br]
                            addr_country]]
                          [:br]
                          [:div [:strong "Tour Type: "] tour_type]
                          [:div [:strong "Frontline Rep: "] frontline_rep]

                          [:div [:strong "Deal Type: "] deal_type]
                          [:div [:strong "Down Payment: "] "$" down_payment]
                          (when (and turn_over (seq turn_over))
                            [:div [:strong "Turn Over: "] turn_over])
                          [:div [:strong "Split?: "] (if (and split (not (zero? split))) "Yes" "No")]]
                         [:div.card-footer
                          [:div.card-footer-item [:div.is-small date]]
                          [:div.card-footer-item [:strong "Contract #: "] contract_num]
                          [:div.card-footer-item [:strong "Member #: "] member_num]]]]))
                   (query-fn :get-deals {}))))]])

(defn create-deal! [& {:keys [query-fn params eid]}]
  (let [{:keys [date name email phone
                street city state zip-code country
                tour-type frontline-rep
                contract member
                deal-type down-payment split to]}
        (h/->map params)]
    (query-fn :add-deal! {:date          date
                          :members       name
                          :email         email
                          :phone         phone
                          :second_phone  nil
                          :addr_street   street
                          :addr_city     city
                          :addr_state    state
                          :addr_zip      zip-code
                          :addr_country  country

                          :tour_type     tour-type
                          :frontline_rep frontline-rep

                          :contract_num  contract
                          :member_num    member
                          :deal_type     deal-type
                          :down_payment  down-payment
                          :turn_over     (when (seq to) to)
                          :split         (and split (contains? #{"on" "true"} split))})
    (ui (all-deals :query-fn query-fn :eid eid))))

(defn deals-routes [{:keys [query-fn]}]
  (let [deals-list-id "deals-list"]
    [["" {:get (fn [_]
                 (page-htmx [:div.columns.is-centered
                             (new-deal :target-id deals-list-id :endpoint "/deals/add")
                             (all-deals :query-fn query-fn :eid deals-list-id)]))}]
     ["/new" {:get (fn [_]
                     (ui (new-deal :target-id deals-list-id :endpoint "/deals/add")))}]
     ["/add" {:post (fn [{:keys [params]}]
                      (create-deal! :query-fn query-fn :params params :eid deals-list-id))}]
     ["/all" {:get (fn [_]
                     (page-htmx [:div.columns.is-centered
                                 (all-deals :query-fn query-fn :eid deals-list-id)]))}]
     ["/down-payment" {:get (fn [{:keys [params]}]
                              (ui (down-payment (:deal-type (h/->map params)))))}]]))
