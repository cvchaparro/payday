(ns io.cvcf.payday.web.htmx
  (:require
   [simpleui.render :as render]
   [ring.util.http-response :as http-response]
   [hiccup.page :as p]))

(defn page [opts & content]
  (-> (p/html5 opts content)
      http-response/ok
      (http-response/content-type "text/html")))

(defn page-htmx [& body]
  (page
   [:head
    [:meta {:charset "UTF-8"}]
    [:title "Wyndham Payday"]
    [:link {:href "/css/all.min.css" :rel "stylesheet" :type "text/css"}]
    [:script {:src "/js/htmx.min.js" :defer true}]
    [:script {:src "/js/tailwind.min.js" :defer true}]]
   [:body (render/walk-attrs body)]))
