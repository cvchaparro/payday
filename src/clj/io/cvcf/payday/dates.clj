(ns io.cvcf.payday.dates)

(defn today [] (java.time.LocalDate/now))

(defn ensure-local-date [date]
  (cond
    (and (string? date) (seq date))        (java.time.LocalDate/parse date)
    (.isInstance java.time.LocalDate date) date))

(defn year
  ([]     (year (today)))
  ([date] (.getYear (ensure-local-date date))))

(defn month
  ([]     (month (today)))
  ([date] (-> (ensure-local-date date) .getMonth .getValue)))

(defn day
  ([]     (day (today)))
  ([date] (.getDayOfMonth (ensure-local-date date))))

(defn months-for-year [year]
  (if (= year (year)) (month) 12))
