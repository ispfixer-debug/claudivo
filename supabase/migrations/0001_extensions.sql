-- Schema extensions and setup
-- Migration 0001 - PLAN.md §6.1

-- Enable required extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS "postgis";
CREATE EXTENSION IF NOT EXISTS "pg_cron";
CREATE EXTENSION IF NOT EXISTS "pg_net";

-- Use pgcrypto for Argon2 password hashing
-- Note: PostgreSQL doesn't have native Argon2, using scram-sha-256 as alternative
-- In production Edge Functions, Argon2 is used via the Deno arg2 module