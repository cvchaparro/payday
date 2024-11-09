(ns io.cvcf.payday.web.controllers.deals
  (:require
   [clojure.string :as s]

   ;; Helpers
   [io.cvcf.payday.dates :as dates]
   [io.cvcf.payday.helpers :as h]))

(def commission-minimum-percentage 0.11)
(def commission-cutoff-percentage  0.12)
(def deal-types {:300k {:name "300k"
                        :min-down 349
                        :down-cutoff 1300
                        :total 3178.5
                        :volume 2770}
                 :400k {:name "400k"
                        :min-down 449
                        :down-cutoff 1400
                        :total 4036.5
                        :volume 3550}})

(defn disco-package->num [package-name]
  (->> (name package-name)
       (take-while Character/isDigit)
       (s/join "")
       Integer/valueOf))

(defn get-deals [db & {:keys [year month day id] :as args}]
  (letfn [(by-year   [y]        (db :get-by-year  {:table-name "deals" :year y}))
          (by-month  [y m]      (db :get-by-month {:table-name "deals" :year y :month m}))
          (by-day    [y m d]    (db :get-by-day   {:table-name "deals" :year y :month m :day d}))
          (by-id     [id]       (db :get-by-id    {:table-name "deals" :id id}))
          (all       []         (db :get-deals    {}))
          (filter-id [id deals] (filterv #(= (:id %) id) deals))]
    (let [{:keys [year month day id]} (h/->map args :val-fn str)
          year  (when (seq year)  year)
          month (when (seq month) month)
          day   (when (seq day)   day)
          id    (when (seq id)    id)]
      (cond
        (and id (not-any? seq [year month day])) (remove nil? [(by-id id)])
        (and year month day id)                  (filter-id id (by-day   year month day))
        (and year month id)                      (filter-id id (by-month year month))
        (and year id)                            (filter-id id (by-year  year))
        (and year month day)                     (by-day   year month day)
        (and year month)                         (by-month year month)
        year                                     (by-year  year)
        :else                                    (all)))))

(defn get-date-part [db part & args]
  (mapv #(format "%02d" %)
        (sort (set (map part (apply get-deals db args))))))

(defn get-years [db]
  (get-date-part db :year))

(defn get-months [db year]
  (get-date-part db :month :year year))

(defn get-days [db year month]
  (get-date-part db :day :year year :month month))

(defn get-ids [db]
  (map :id (get-deals db)))

(defn get-min-id
  "Return the minimum deal ID."
  [db]
  (when-let [ids (seq (get-ids db))]
    (apply min ids)))

(defn get-max-id
  "Return the maximum deal ID."
  [db]
  (when-let [ids (seq (get-ids db))]
    (apply max ids)))

(defn save-deal! [db params]
  (let [{:keys [date name
                primary-email secondary-email
                primary-phone secondary-phone
                street city state zip-code country
                tour-type frontline-rep
                contract member
                deal-type down-payment split to]}
        (h/->map params)]
    (db :add-deal! {:year             (dates/year  date)
                    :month            (dates/month date)
                    :day              (dates/day   date)
                    :members          name
                    :primary-email    primary-email
                    :secondary-email  (when (seq secondary-email) secondary-email)
                    :primary-phone    primary-phone
                    :secondary-phone  (when (seq secondary-phone) secondary-phone)
                    :street           street
                    :city             city
                    :state            state
                    :zip              zip-code
                    :country          country

                    :tour-type        tour-type
                    :frontline-rep    frontline-rep

                    :contract-num     contract
                    :member-num       member
                    :deal-type        deal-type
                    :down-payment     down-payment
                    :turn-over        (when (seq to) to)
                    :split            (and split (contains? #{"on" "true"} split))})))
