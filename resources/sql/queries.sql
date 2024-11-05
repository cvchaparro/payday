-- Place your queries here. Docs available https://www.hugsql.org/

-- :name add-deal! :! :n
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
  :email,
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


-- :name remove-deal-by-id! :! :n
-- :doc Remove a deal by the specified ID
DELETE FROM deals
 WHERE
   id = :id;


-- :name remove-deals! :! :n
-- :doc Remove all deals
DELETE FROM deals;


-- :name get-deal-by-id :? :1
-- :doc Return the deal with the given ID
SELECT * FROM deals
 WHERE
   id = :id;


-- :name get-deals-by-year :? :*
-- :doc Return all the deals for a given year
SELECT * FROM deals
 WHERE
   year = :year;


-- :name get-deals-by-month :? :*
-- :doc Return all the deals for a given month
SELECT * FROM deals
 WHERE
   year  = :year AND
   month = :month;

-- :name get-deals-by-day :? :*
-- :doc Return all the deals for a given day
SELECT * FROM deals
 WHERE
   year  = :year AND
   month = :month AND
   day   = :day;


-- :name get-deals :? :*
-- :doc Return all the deals
SELECT * FROM deals;
