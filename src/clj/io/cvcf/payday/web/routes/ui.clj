(ns io.cvcf.payday.web.routes.ui
  (:require
   [integrant.core :as ig]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]

   ;; Middleware
   [io.cvcf.payday.web.middleware.exception :as exception]
   [io.cvcf.payday.web.middleware.formats :as formats]

   ;; Routes
   [io.cvcf.payday.web.views.deals :as deals]))

(defn route-data [opts]
  (merge
   opts
   {:muuntaja   formats/instance
    :middleware
    [;; Default middleware for ui
     ;; query-params & form-params
     parameters/parameters-middleware
     ;; encoding response body
     muuntaja/format-response-middleware
     ;; exception handling
     exception/wrap-exception]}))

(derive :reitit.routes/ui :reitit/routes)

(defn page-routes [{:keys [base-path] :as opts}]
  [["/deals" (deals/deals-routes base-path)]])

(defmethod ig/init-key :reitit.routes/ui
  [_ {:keys [base-path]
      :or   {base-path ""}
      :as   opts}]
  (fn [] [base-path (route-data opts) (page-routes opts)]))
