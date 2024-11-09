(ns io.cvcf.payday.web.controllers.calculator
  (:require
   [io.cvcf.payday.web.controllers.deals :as deals]))

(defn get-deal-type [deal]
  ((comp #(get deals/deal-types %) keyword :deal_type) deal))

(defn get-volume
  [db & {:keys [year month day id]}]
  (map (comp :volume get-deal-type)
       (deals/get-deals db :year year :month month :day day :id id)))

(defn get-commission
  [db & {:keys [year month day id]}]
  (map (fn [deal]
         (let [deal-type (get-deal-type [deal])
               down   (:down_payment deal)
               cutoff (:down-cutoff deal-type)
               volume (:volume deal-type)]
           (* volume
              (if (>= down cutoff)
                deals/commission-cutoff-percentage
                deals/commission-minimum-percentage))))
       (deals/get-deals db :year year :month month :day day :id id)))

(defn calculate-volume
  "Calculate the volume accumulated for the given time."
  [db & {:keys [year month day id]}]
  (reduce + (get-volume db :year year :month month :day day :id id)))

(defn calculate-commission
  "Calculate the commission accumulated for the given time."
  [db & {:keys [year month day id]}]
  (reduce + (get-commission db :year year :month month :day day :id id)))
