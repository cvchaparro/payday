(ns io.cvcf.payday.web.views.deals
  (:require
   [clojure.string :as s]
   [io.cvcf.payday.web.views.components :as c]
   [io.cvcf.payday.web.htmx :refer [page-htmx ui]]
   [simpleui.core :as simpleui :refer [defcomponent]]))

(def deal-types {:300k {:name "300k"
                        :min-down 349
                        :down-cutoff 1300
                        :total 3178.5}
                 :400k {:name "400k"
                        :min-down 449
                        :down-cutoff 1400
                        :total 4036.5}})

(defn disco-package->num [package-name]
  (->> (name package-name)
       (take-while Character/isDigit)
       (s/join "")
       Integer/valueOf))

(defn down-payment-input [selected]
  (let [selected (keyword selected)
        minimum (get-in deal-types [selected :min-down])
        maximum (get-in deal-types [selected :total])]
    [:input#down-payment.input
     {:type "number"
      :name "down-payment"
      :placeholder "e.g. 1234"
      :step "0.01"
      :min minimum
      :max maximum
      :required true}]))

(defcomponent ^:endpoint new-deal [req target-id endpoint]
  [:div.column
   [:h3.title.is-3 "New deal"]
   [:form {:id id
           :hx-post   endpoint
           :hx-target (c/id target-id)
           :hx-swap   "outerHTML"}
    [:div.field
     [:label.label.subtitle.is-4 "Date"]
     [:input.input {:type "date"
                    :name "date"
                    :required true}]]

    [:div.field
     [:label.label.subtitle.is-4 "Guest Information"]
     [:div.field
      [:label.label "Customer Name"]
      [:div.control
       [:input.input {:type "text"
                      :name "name"
                      :placeholder "e.g. John Smith"
                      :required true}]]]

     [:div.field
      [:label.label.subtitle.is-5 "Contact Information"]
      [:div.field
       [:label.label "Email"]
       [:input.input {:type "email"
                      :name "email"
                      :placeholder "e.g. john.smith@email.com"
                      :required true}]]

      [:div.field
       [:label.label "Phone"]
       [:input.input {:type "phone"
                      :name "phone"
                      :placeholder "e.g. 123.456.7890"}]]]

     [:div.field
      [:label.label.subtitle.is-6 "Address"]
      [:div.field
       [:label.label "Street"]
       [:input.input {:type "text"
                      :name "street"
                      :placeholder "e.g. 123 Main St"
                      :required true}]]

      [:div.field
       [:label.label "City"]
       [:input.input {:type "text"
                      :name "city"
                      :placeholder "e.g. Anycity"
                      :required true}]]

      [:div.field
       [:label.label "State"]
       [:input.input {:type "text"
                      :name "state"
                      :placeholder "e.g. Anystate"
                      :required true}]]

      [:div.field
       [:label.label "Zip code"]
       [:input.input {:type "number"
                      :name "zip"
                      :placeholder "e.g. 12345"
                      :required true}]]

      [:div.field
       [:label.label "Country"]
       [:input.input {:type "text"
                      :name "country"
                      :placeholder "e.g. USA"
                      :required true}]]]]

    [:div.field
     [:label.label.subtitle.is-4 "Tour Information"]
     [:div.field
      [:div.control
       [:label.label "Tour type"]
       [:input.input {:type "input"
                      :name "tour-type"
                      :placeholder "e.g. Monster"
                      :required true}]]]
     [:div.field
      [:div.control
       [:label.label "Frontline rep"]
       [:input.input {:type "input"
                      :name "rep"
                      :placeholder "e.g. Jon Bon Jovi"
                      :required true}]]]]

    [:div.field
     [:label.label.subtitle.is-4 "Member Information"]
     [:div.field
      [:div.control
       [:label.label "Contract #"]
       [:input.input {:type "text"
                      :name "contract"
                      :placeholder "e.g. 00065..."
                      :required true}]]]
     [:div.field
      [:div.control
       [:label.label "Member #"]
       [:input.input {:type "text"
                      :name "member"
                      :placeholder "e.g. 00203..."
                      :required true}]]]]

    (let [deal-names (keys deal-types)
          values (map name deal-names)
          selected (format "%dk" (apply max (map disco-package->num deal-names)))]
      [:div
       [:div.field
        (c/select-box "Deal Type" values
                      :target-id "down-payment"
                      :endpoint  "/deals/down-payment"
                      :selected  selected)]

       [:div.field
        [:div.control
         [:label.label "Down Payment"]
         (down-payment-input (keyword selected))]]])

    [:div.field
     [:div.control
      [:label.label "TO"]
      [:input.input {:type "text"
                     :name "to"
                     :placeholder "e.g. Cameron Diaz"}]]]

    [:div.field
     [:div.control
      [:label.checkbox [:input {:type "checkbox" :name "split"}] " Split?"]]]

    [:div.field
     [:div.control
      [:input.button.is-primary
       {:type "submit" :value "Add"}]]]]])

(defcomponent down-payment [req selected]
  (down-payment-input selected))

(defcomponent all [req & {:keys [elt-id]}]
  [:div.column {:id elt-id}
   [:h3.title.is-3 "Deals"]
   [:p (:name req)]
   [:p (:email req)]
   [:p (:phone req)]])

(defn ->map [m]
  (zipmap (map #(keyword (s/replace %1 #"\s+" "-")) (keys m))
          (vals m)))

(defn deals-routes []
  (let [deals-list-id "deals-list"]
    [["" (fn [req]
           (page-htmx [:div.columns
                       (new-deal req deals-list-id "/deals/all")
                       (all req :elt-id deals-list-id)]))]
     ["/all" (fn [{:keys [form-params]}]
               (ui (all (->map form-params) :elt-id deals-list-id)))]
     ["/down-payment" (fn [{:keys [params]}]
                        (ui (down-payment nil (:deal-type (->map params)))))]]))
