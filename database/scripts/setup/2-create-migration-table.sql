CREATE TABLE IF NOT EXISTS migration (
  migration_id       SERIAL PRIMARY KEY,
  last_run_migration TEXT UNIQUE
);