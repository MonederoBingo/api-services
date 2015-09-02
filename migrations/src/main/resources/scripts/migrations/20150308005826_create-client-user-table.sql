CREATE TABLE IF NOT EXISTS client_user(
  client_user_id SERIAL PRIMARY KEY,
  client_id INT REFERENCES client (client_id) UNIQUE,
  name TEXT,
  email TEXT UNIQUE,
  password TEXT,
  sms_key TEXT
);