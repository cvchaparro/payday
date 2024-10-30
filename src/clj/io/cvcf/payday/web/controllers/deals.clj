(ns io.cvcf.payday.web.controllers.deals
  (:require
   [clojure.string :as s]))

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
