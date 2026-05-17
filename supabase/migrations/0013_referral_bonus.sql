-- Add referral tracking
ALTER TABLE vito_referrals ADD COLUMN IF NOT EXISTS bonus_amount INTEGER DEFAULT 100;
ALTER TABLE vito_referrals ADD COLUMN IF NOT EXISTS bonus_credited BOOLEAN DEFAULT false;
ALTER TABLE vito_referrals ADD COLUMN IF NOT EXISTS credited_at TIMESTAMP;
