-- Add wallet balance column
ALTER TABLE vito_wallets ADD COLUMN IF NOT EXISTS balance INTEGER DEFAULT 0;
ALTER TABLE vito_wallets ADD COLUMN IF NOT EXISTS currency VARCHAR(3) DEFAULT 'PHP';
