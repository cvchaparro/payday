CREATE TABLE IF NOT EXISTS days (
  year      INTEGER NOT NULL,
  month     INTEGER NOT NULL,
  day       INTEGER NOT NULL,
  tours     INTEGER NOT NULL DEFAULT 0,
  freeshots INTEGER NOT NULL DEFAULT 0,

  PRIMARY KEY (year, month, day)
);
