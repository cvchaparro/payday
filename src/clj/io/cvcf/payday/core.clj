(ns io.cvcf.payday.core
  (:require
   [clojure.tools.logging :as log]
   [integrant.core :as ig]
   [io.cvcf.payday.config :as config]
   [io.cvcf.payday.env :refer [defaults]]

   ;; Edges
   [kit.edge.server.undertow]
   [io.cvcf.payday.web.handler]

   ;; Routes
   [io.cvcf.payday.web.routes.api]
   [io.cvcf.payday.web.routes.ui]
   [kit.edge.db.sql.conman]
   [kit.edge.db.sql.migratus])
  (:gen-class))

;; log uncaught exceptions in threads
(Thread/setDefaultUncaughtExceptionHandler
 (reify Thread$UncaughtExceptionHandler
   (uncaughtException [_ thread ex]
     (log/error {:what :uncaught-exception
                 :exception ex
                 :where (str "Uncaught exception on" (.getName thread))}))))

(defonce system (atom nil))

(defn stop-app []
  ((or (:stop defaults) (fn [])))
  (some-> (deref system) (ig/halt!))
  (shutdown-agents))

(defn start-app [& [params]]
  ((or (:start params) (:start defaults) (fn [])))
  (->> (config/system-config (or (:opts params) (:opts defaults) {}))
       (ig/expand)
       (ig/init)
       (reset! system))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))

(defn -main [& _]
  (start-app))
