-- Add rating columns
ALTER TABLE vito_jobs ADD COLUMN IF NOT EXISTS client_rating INTEGER;
ALTER TABLE vito_jobs ADD COLUMN IF NOT EXISTS client_rating_comment TEXT;
ALTER TABLE vito_jobs ADD COLUMN IF NOT EXISTS driver_rating INTEGER;
ALTER TABLE vito_jobs ADD COLUMN IF NOT EXISTS driver_rating_comment TEXT;
