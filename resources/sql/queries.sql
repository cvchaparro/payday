-- Place your queries here. Docs available https://www.hugsql.org/

-- :name add-deal! :! :1
-- :doc Add a new deal
INSERT INTO deals (
  year,
  month,
  day,
  members,
  primaryemail,
  secondaryemail,
  primaryphone,
  secondaryphone,

  street,
  city,
  state,
  zip,
  country,

  tourtype,
  frontlinerep,

  contractnum,
  membernum,
  dealtype,
  downpayment,
  turnover,
  split
) VALUES (
  :year,
  :month,
  :day,
  :members,
  :primary-email,
  :secondary-email,
  :primary-phone,
  :secondary-phone,
  :street,
  :city,
  :state,
  :zip,
  :country,

  :tour-type,
  :frontline-rep,

  :contract-num,
  :member-num,
  :deal-type,
  :down-payment,
  :turn-over,
  :split
);


-- :name remove-deal-by-id! :! :1
-- :doc Remove a deal by the specified ID
DELETE FROM deals
 WHERE
   id = :id;


-- :name remove-deals! :! :*
-- :doc Remove all deals
DELETE FROM deals;


-- :name get-by-id :? :1
-- :doc Return the deal with the given ID
SELECT * FROM :i:table-name
 WHERE
   id = :id;


-- :name get-by-year :? :*
-- :doc Return data for a given year
SELECT * FROM :i:table-name
 WHERE
   year = :year;


-- :name get-by-month :? :*
-- :doc Return data for a given month
SELECT * FROM :i:table-name
 WHERE
   year  = :year AND
   month = :month;


-- :name get-by-day :? :*
-- :doc Return data for a given day
SELECT * FROM :i:table-name
 WHERE
   year  = :year AND
   month = :month AND
   day   = :day;


-- :name get-deals :? :*
-- :doc Return all the deals
SELECT * FROM deals;
