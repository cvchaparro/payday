CREATE TABLE IF NOT EXISTS kicks (
  id             INTEGER       PRIMARY KEY AUTOINCREMENT,
  dealid         INTEGER       UNIQUE NOT NULL,
  kickyear       INTEGER       NOT NULL,
  kickmonth      INTEGER       NOT NULL,
  kickday        INTEGER       NOT NULL,
  paycancelyear  INTEGER       NOT NULL,
  paycancelmonth INTEGER       NOT NULL,
  paycancelday   INTEGER       NOT NULL,
  reason         VARCHAR(5000) NOT NULL,

  CONSTRAINT fk_deal_id FOREIGN KEY (dealid) REFERENCES deals(id)
);
