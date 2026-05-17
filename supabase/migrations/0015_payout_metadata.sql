-- Payout metadata
ALTER TABLE vito_payout_requests ADD COLUMN IF NOT EXISTS bank_name VARCHAR(100);
ALTER TABLE vito_payout_requests ADD COLUMN IF NOT EXISTS bank_account VARCHAR(50);
ALTER TABLE vito_payout_requests ADD COLUMN IF NOT EXISTS processed_at TIMESTAMP;
ALTER TABLE vito_payout_requests ADD COLUMN IF NOT EXISTS processed_by VARCHAR(50);
