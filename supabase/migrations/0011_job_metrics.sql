-- Add job metrics
ALTER TABLE vito_jobs ADD COLUMN IF NOT EXISTS distance_km DECIMAL(6,2);
ALTER TABLE vito_jobs ADD COLUMN IF NOT EXISTS duration_min INTEGER;
ALTER TABLE vito_jobs ADD COLUMN IF NOT EXISTS estimated_pickup_min INTEGER;
ALTER TABLE vito_jobs ADD COLUMN IF NOT EXISTS actual_pickup_time TIMESTAMP;
ALTER TABLE vito_jobs ADD COLUMN IF NOT EXISTS actual_completion_time TIMESTAMP;
