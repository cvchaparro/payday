(ns io.cvcf.payday.web.views.components
  (:require
   [clojure.string :as s]))

(defn id [n] (format "#%s" n))

(defn select-box [label values & {:keys [target-id endpoint selected] :as opts}]
  [:div
   [:label.label label]
   [:div.control
    [:div.select
     (letfn [(attributes [attrs]
               (merge attrs
                      (when target-id
                        {:hx-target (id target-id)
                         :hx-get    endpoint
                         :hx-swap   "outerHTML"})))]
       (let [n (s/lower-case label)]
         (into [] (concat [:select (attributes {:id n :name n})
                           [:option {:selected (= selected label)} label]]
                          (map #(vec [:option {:value %1 :selected (= selected %1)} %1]) values)))))]]])
