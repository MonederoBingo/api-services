CREATE TABLE IF NOT EXISTS dummy(
   id SERIAL PRIMARY KEY,
   name TEXT,
   value TEXT
);
INSERT INTO dummy(name, value) VALUES('A','B'), ('C','D');