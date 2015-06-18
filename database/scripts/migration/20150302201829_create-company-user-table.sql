CREATE TABLE IF NOT EXISTS company_user(
  company_user_id SERIAL PRIMARY KEY,
  company_id     INT     NOT NULL REFERENCES company (company_id),
  name           TEXT    NOT NULL,
  email TEXT NOT NULL UNIQUE,
  password       TEXT    NOT NULL,
  active         BOOLEAN NOT NULL DEFAULT FALSE,
  activation_key TEXT UNIQUE,
  language TEXT
);