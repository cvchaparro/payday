CREATE TABLE IF NOT EXISTS deals (
  id              INTEGER       PRIMARY KEY AUTOINCREMENT,
  year            INTEGER       NOT NULL,
  month           INTEGER       NOT NULL,
  day             INTEGER       NOT NULL,
  members         VARCHAR(1000) NOT NULL,
  primaryemail    VARCHAR(100)  NOT NULL,
  secondaryemail  VARCHAR(100),
  primaryphone    VARCHAR(30)   NOT NULL,
  secondaryphone  VARCHAR(30),
  street          VARCHAR(100)  NOT NULL,
  city            VARCHAR(100)  NOT NULL,
  state           VARCHAR(30)   NOT NULL,
  zip             VARCHAR(30)   NOT NULL,
  country         VARCHAR(30)   NOT NULL DEFAULT "USA",

  tourtype        VARCHAR(30)   NOT NULL,
  frontlinerep    VARCHAR(100)  NOT NULL,

  contractnum     VARCHAR(30)   NOT NULL,
  membernum       VARCHAR(30)   NOT NULL,
  dealtype        VARCHAR(10)   NOT NULL,
  downpayment     REAL          NOT NULL,
  turnover        VARCHAR(100),
  split           BOOLEAN
);
