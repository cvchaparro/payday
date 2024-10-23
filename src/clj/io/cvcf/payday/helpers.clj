(ns io.cvcf.payday.helpers
  (:require
   [clojure.string :as s]))

(defn ->map [m]
  (zipmap (map #(keyword (s/replace %1 #"\s+" "-")) (keys m))
          (vals m)))
