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
  `[:div.field-group
    [:label.label.subtitle.is-4 ~label]
    ~@body])

(defn input [name & {:keys [eid type placeholder required? classes extra]
                     :or   {eid         (-> name s/lower-case (s/replace #"\s+" "-"))
                            type        "text"
                            placeholder (-> name s/capitalize (s/replace #"-" " "))
                            required?   true
                            classes     ["input"]}}]
  [:input
   (attributes
    {:id eid :type type :name name}
    (when placeholder {:placeholder placeholder})
    (when required?   {:required    required?})
    (when classes     {:class       (classes->str ["input"] classes)})
    extra)])

(defn options [label values selected]
  (into [[:option {:value "" :selected (= selected label)} label]]
        (map #(vec [:option {:value % :selected (= selected %)} %]) values)))

(defn select [label values & {:keys [target-id endpoint selected extra]}]
  [:div.select
   (let [n (->> (-> label s/lower-case (s/split #" ")) (s/join "-"))]
     (into [:select (attributes {:id n :name n}
                                (when target-id {:hx-target (id target-id) :hx-swap "outerHTML"})
                                (when endpoint  {:hx-get endpoint})
                                extra)]
           (options label values selected)))])

(defn checkbox [label]
  (let [label (s/trim label)
        name  (-> label s/lower-case (s/escape {\? "" \  "-"}))]
    [:div.field
     [:div.control
      [:label.checkbox (input name :type "checkbox" :placeholder nil :classes nil :required? nil)
       (format " %s" label)]]]))

(defn button [label & {:keys [type classes extra]
                       :or   {type    "button"
                              classes ["button"]}}]
  [:div.field
   [:div.control
    [:input (attributes {:class (classes->str ["button"] classes) :type type :value label} extra)]]])
