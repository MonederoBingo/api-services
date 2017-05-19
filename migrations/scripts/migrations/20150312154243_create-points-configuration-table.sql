CREATE TABLE IF NOT EXISTS points_configuration (
  points_configuration_id SERIAL PRIMARY KEY,
  company_id INT UNIQUE,
  points_to_earn          DECIMAL NOT NULL,
  required_amount DECIMAL NOT NULL
);
