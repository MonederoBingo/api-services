CREATE TABLE IF NOT EXISTS promotion (
  promotion_id SERIAL PRIMARY KEY,
  company_id   INT REFERENCES company (company_id),
  client_id    INT REFERENCES client (client_id),
  description  TEXT    NOT NULL,
  used_points  DECIMAL NOT NULL,
  date         TIMESTAMP WITH TIME ZONE
);