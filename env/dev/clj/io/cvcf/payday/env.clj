(ns io.cvcf.payday.env
  (:require
    [clojure.tools.logging :as log]
    [io.cvcf.payday.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init       (fn []
                 (log/info "\n-=[payday starting using the development or test profile]=-"))
   :start      (fn []
                 (log/info "\n-=[payday started successfully using the development or test profile]=-"))
   :stop       (fn []
                 (log/info "\n-=[payday has shut down successfully]=-"))
   :middleware wrap-dev
   :opts       {:profile       :dev
                :persist-data? true}})
