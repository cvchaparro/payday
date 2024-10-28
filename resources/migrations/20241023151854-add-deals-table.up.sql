CREATE TABLE IF NOT EXISTS deals (
  id            INTEGER       PRIMARY KEY AUTOINCREMENT,
  year          INTEGER       NOT NULL,
  month         INTEGER       NOT NULL,
  day           INTEGER       NOT NULL,
  members       VARCHAR(1000) NOT NULL,
  email         VARCHAR(100)  NOT NULL,
  phone         VARCHAR(30)   NOT NULL,
  second_phone  VARCHAR(30),
  addr_street   VARCHAR(100)  NOT NULL,
  addr_city     VARCHAR(100)  NOT NULL,
  addr_state    VARCHAR(30)   NOT NULL,
  addr_zip      VARCHAR(30)   NOT NULL,
  addr_country  VARCHAR(30)   NOT NULL DEFAULT "USA",

  tour_type     VARCHAR(30)   NOT NULL,
  frontline_rep VARCHAR(100)  NOT NULL,

  contract_num  VARCHAR(30)   NOT NULL,
  member_num    VARCHAR(30)   NOT NULL,
  deal_type     VARCHAR(10)   NOT NULL,
  down_payment  REAL          NOT NULL,
  turn_over     VARCHAR(100),
  split         BOOLEAN
);
