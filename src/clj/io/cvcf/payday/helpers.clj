(ns io.cvcf.payday.helpers
  (:require
   [clojure.string :as s]))

(defn ->map [m & {:keys [key-fn]
                  :or   {key-fn identity}}]
  (zipmap (map #(let [n (key-fn %)]
                  (if (keyword? n) n (keyword (s/replace n #"\s+" "-"))))
               (keys m))
          (vals m)))
