-- Add driver location tracking
ALTER TABLE vito_drivers ADD COLUMN IF NOT EXISTS current_lat DECIMAL(10,8);
ALTER TABLE vito_drivers ADD COLUMN IF NOT EXISTS current_lng DECIMAL(11,8);
ALTER TABLE vito_drivers ADD COLUMN IF NOT EXISTS last_location_update TIMESTAMP;
ALTER TABLE vito_drivers ADD COLUMN IF NOT EXISTS is_online BOOLEAN DEFAULT false;
