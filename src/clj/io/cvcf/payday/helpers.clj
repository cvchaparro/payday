(ns io.cvcf.payday.helpers
  (:require
   [clojure.string :as s]))

(defn ->map [m]
  (zipmap (map #(if (keyword? %) % (keyword (s/replace % #"\s+" "-")))
               (keys m))
          (vals m)))
