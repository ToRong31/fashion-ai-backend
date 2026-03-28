-- DEV ONLY: Wipe all tables. DO NOT run in production.
-- Use when you want a completely fresh database.
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
GRANT ALL ON SCHEMA public TO PUBLIC;
