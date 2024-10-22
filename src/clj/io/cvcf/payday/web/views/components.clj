(ns io.cvcf.payday.web.views.components
  (:require
   [clojure.string :as s]))

(defn id [n] (format "#%s" (s/replace n #"#" "")))
(defn attributes [defaults & extras] (apply merge defaults extras))
(defn classes->str [defaults & extras] (s/join " " (set (apply conj defaults (flatten extras)))))

(defmacro field [label & body]
  `[:div.field
    [:label.label ~label]
    [:div.control ~@body]])

(defmacro field-group [label & body]
  `[:div.field
    [:label.label.subtitle.is-4 ~label]
    ~@body])

(defn input [name & {:keys [eid type  placeholder required? classes nothing? extra]
                     :or   {eid         (s/replace name #"\s+" "-")
                            type        "text"
                            placeholder (s/capitalize name)
                            required?   true
                            classes     ["input"]}}]
  [:input
   (attributes
    {:id (id eid) :type type :name name}
    (when placeholder {:placeholder placeholder})
    (when required?   {:required    required?})
    (when classes     {:class       (classes->str ["input"] classes)})
    extra)])

(defn select [label values & {:keys [target-id endpoint selected]}]
  [:div.select
   (let [n (s/lower-case label)]
     (into [] (concat [:select (attributes {:id n :name n}
                                           {:hx-target (id target-id) :hx-get endpoint :hx-swap "outerHTML"})
                       [:option {:selected (= selected label)} label]]
                      (map #(vec [:option {:value %1 :selected (= selected %1)} %1]) values))))])

(defn checkbox [label]
  (let [label (s/trim label)
        name  (-> label s/lower-case (s/escape {\? "" \  "-"}))]
    [:div.field
     [:div.control
      [:label.checkbox (input name :type "checkbox" :placeholder nil :classes nil :required? nil)
       (format " %s" label)]]]))

(defn button [label & {:keys [type classes]
                       :or   {type    "button"
                              classes ["button"]}}]
  [:div.field
   [:div.control
    [:input {:class (classes->str ["button"] classes) :type type :value label}]]])
