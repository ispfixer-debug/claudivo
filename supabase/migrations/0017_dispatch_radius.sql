-- Dispatch settings
ALTER TABLE vito_feature_flags ADD COLUMN IF NOT EXISTS dispatch_radius_km INTEGER DEFAULT 3;
ALTER TABLE vito_feature_flags ADD COLUMN IF NOT EXISTS max_dispatch_distance_km INTEGER DEFAULT 10;
ALTER TABLE vito_feature_flags ADD COLUMN IF NOT EXISTS dispatch_widening_enabled BOOLEAN DEFAULT true;
