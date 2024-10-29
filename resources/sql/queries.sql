-- Place your queries here. Docs available https://www.hugsql.org/

-- :name add-deal! :! :n
-- :doc Add a new deal
INSERT INTO deals (
  year,
  month,
  day,
  members,
  email,
  phone,
  second_phone,
  addr_street,
  addr_city,
  addr_state,
  addr_zip,
  addr_country,

  tour_type,
  frontline_rep,

  contract_num,
  member_num,
  deal_type,
  down_payment,
  turn_over,
  split
) values (
  :year,
  :month,
  :day,
  :members,
  :email,
  :phone,
  :second-phone,
  :addr-street,
  :addr-city,
  :addr-state,
  :addr-zip,
  :addr-country,

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


-- :name get-deals :? :*
-- :doc Return all the deals
SELECT * FROM deals;
