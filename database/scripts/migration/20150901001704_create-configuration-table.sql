CREATE TABLE IF NOT EXISTS configuration (
  configuration_id SERIAL PRIMARY KEY,
  name             TEXT UNIQUE,
  description      TEXT,
  value            TEXT
);


