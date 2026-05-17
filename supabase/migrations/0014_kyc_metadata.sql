-- KYC metadata
ALTER TABLE vito_kyc_documents ADD COLUMN IF NOT EXISTS expiry_date DATE;
ALTER TABLE vito_kyc_documents ADD COLUMN IF NOT EXISTS rejection_reason TEXT;
ALTER TABLE vito_kyc_documents ADD COLUMN IF NOT EXISTS reviewed_by VARCHAR(50);
