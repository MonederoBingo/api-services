CREATE TABLE IF NOT EXISTS promotion_configuration (
  promotion_configuration_id SERIAL PRIMARY KEY,
  company_id INT,
  description     TEXT    NOT NULL,
  required_points DECIMAL NOT NULL
);
