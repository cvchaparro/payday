(ns io.cvcf.payday.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init       (fn []
                 (log/info "\n-=[payday starting]=-"))
   :start      (fn []
                 (log/info "\n-=[payday started successfully]=-"))
   :stop       (fn []
                 (log/info "\n-=[payday has shut down successfully]=-"))
   :middleware (fn [handler _] handler)
   :opts       {:profile :prod}})
