-- Cancellation fee configuration
ALTER TABLE vito_jobs ADD COLUMN IF NOT EXISTS cancellation_fee_applied INTEGER DEFAULT 0;
ALTER TABLE vito_jobs ADD COLUMN IF NOT EXISTS cancellation_reason_code VARCHAR(20);
CREATE TABLE IF NOT EXISTS vito_cancellation_fees (
  id SERIAL PRIMARY KEY,
  reason_code VARCHAR(20) UNIQUE NOT NULL,
  fee_amount INTEGER DEFAULT 0,
  applies_after_min INTEGER DEFAULT 2
);
INSERT INTO vito_cancellation_fees (reason_code, fee_amount, applies_after_min) VALUES 
  ('driver_no_show', 50, 3),
  ('client_cancel', 25, 2),
  ('changed_mind', 0, 0);
