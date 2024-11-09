(ns io.cvcf.payday.helpers
  (:require
   [clojure.string :as s]))

(defn ->map [m & {:keys [key-fn val-fn]
                  :or   {key-fn identity val-fn identity}}]
  (zipmap (map #(let [n (key-fn %)]
                  (if (keyword? n) n (keyword (s/replace n #"\s+" "-"))))
               (keys m))
          (map val-fn (vals m))))
