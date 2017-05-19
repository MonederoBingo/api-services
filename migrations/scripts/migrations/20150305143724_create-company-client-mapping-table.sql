CREATE TABLE IF NOT EXISTS company_client_mapping(
  company_client_mapping_id SERIAL PRIMARY KEY,
  company_id INT,
  client_id INT,
  points DECIMAL,
  UNIQUE (company_id, client_id)
);
