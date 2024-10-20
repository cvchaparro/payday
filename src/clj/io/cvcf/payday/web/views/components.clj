(ns io.cvcf.payday.web.views.components
  (:require
   [clojure.string :as s]))

(defn select [label values & {:keys [target-id selected]}]
  (letfn [(attributes [attrs]
            (merge attrs
                   (when target-id
                     {:hx-target (str "#" target-id)
                      :hx-get    (str "/" target-id)
                      :hx-swap   "outerHTML"})))]
    (let [n (s/lower-case label)]
      (into [] (concat [:select (attributes {:id n :name n})
                        [:option {:selected (= selected label)} label]]
                       (map #(vec [:option {:value %1 :selected (= selected %1)} %1]) values))))))

(defn select-box [label values & {:keys [target-id selected] :as opts}]
  [:div
   [:label.label label]
   [:div.control
    [:div.select (select label values opts)]]])
