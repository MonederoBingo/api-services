CREATE TABLE IF NOT EXISTS company_client_mapping(
  company_client_mapping_id SERIAL PRIMARY KEY,
  company_id INT REFERENCES company(company_id),
  client_id INT REFERENCES client (client_id),
  points DECIMAL,
  UNIQUE (company_id, client_id)
);