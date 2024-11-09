(ns io.cvcf.payday.web.views.calculator
  (:require
   [io.cvcf.payday.web.htmx :refer [page-htmx page]]
   [io.cvcf.payday.web.views.components :as c]

   ;; Controllers
   [io.cvcf.payday.web.controllers.calculator :as calc]
   [io.cvcf.payday.web.controllers.deals :as deals]))

(defn get-values [func params]
  (if (every? Character/isDigit (apply str params))
    (func)
    []))

(defn get-years-select [{:keys [db]}]
  (c/select "Year" (get-values #(deals/get-years db) nil)
            :target-id "month"
            :endpoint  "/calc/month"))

(defn get-months-select [{:keys [db params values]}]
  (let [{:keys [year]} params]
    (c/select "Month" (or values (get-values #(deals/get-months db year) (vals params)))
              :target-id "day"
              :endpoint  "/calc/day"
              :extra     {:hx-include "#year, #month"})))

(defn get-days-select [{:keys [db params values]}]
  (let [{:keys [year month]} params]
    (c/select "Day" (or values (get-values #(deals/get-days db year month) (vals params))))))

(defn calculator [& {:keys [db]}]
  [:div.columns.is-centered
   [:div.column.is-two-fifths
    [:h3.title.is-3 "Calculate Pay"]
    [:div.box
     [:form#select-deals {:hx-target (c/id "data") :hx-post "/calc/calculate"}
      (c/field-group "Date"
        (get-years-select  {:db db})
        (get-months-select {:db db :params {} :values []})
        (get-days-select   {:db db :params {} :values []}))

      [:br]

      (c/field-group "ID"
        (c/input "deal-id"
                 :type      "number"
                 :required? false
                 :extra     {:min (deals/get-min-id db)
                             :max (deals/get-max-id db)}))

      [:div.is-horizontal
       (c/button "Get" :classes "is-info" :extra {:hx-target (c/id "deals-list")
                                                  :hx-post "/deals/get"
                                                  :hx-include (c/id "select-deals")})
       (c/button "Calculate" :type "submit" :classes "is-primary")]]]
    [:div#deals-list]]
   [:div#data.column.is-two-fifths]])

(defn income [db params]
  [:div.fixed-grid.has-2-cols
   [:div.grid
    [:div.cell [:strong "Commission"]]
    [:div.cell "$" (calc/calculate-commission db params)]

    [:div.cell [:strong "Volume"]]
    [:div.cell "$" (calc/calculate-volume db params)]]])

(defn calculator-routes [{:keys [query-fn]}]
  [[""           {:get  (fn [_]                (page-htmx (calculator :db query-fn)))}]
   ["/month"     {:get  (fn [{:keys [params]}] (page (get-months-select {:db query-fn :params params})))}]
   ["/day"       {:get  (fn [{:keys [params]}] (page (get-days-select {:db query-fn :params params})))}]
   ["/calculate" {:post (fn [{:keys [params]}] (page
                                                [:div#data.column.is-two-fifths
                                                 (income query-fn params)]))}]])
