-- Updated at trigger for all tables
-- Migration 0010
CREATE OR REPLACE FUNCTION update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Attach to tables with updated_at
CREATE TRIGGER update_vito_users_updated_at
    BEFORE UPDATE ON vito_users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();

CREATE TRIGGER update_vito_jobs_updated_at
    BEFORE UPDATE ON vito_jobs
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();

CREATE TRIGGER update_vito_wallets_updated_at
    BEFORE UPDATE ON vito_wallets
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();

CREATE TRIGGER update_vito_mart_orders_updated_at
    BEFORE UPDATE ON vito_mart_orders
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();

-- Feature flag defaults
-- Seeding migration 0011
INSERT INTO vito_feature_flags (key, value, description) VALUES
    ('mart_enabled', 'true', 'Enable VitoMart functionality'),
    ('ride_enabled', 'true', 'Enable RIDE service type'),
    ('send_enabled', 'true', 'Enable SEND package delivery'),
    ('base_fare_cents', '250', 'Base fare in cents'),
    ('per_km_cents', '100', 'Per kilometer charge in cents'),
    ('per_min_cents', '25', 'Per minute charge in cents'),
    ('platform_fee_percent', '15', 'Platform fee percentage'),
    ('cancellation_fee_cents', '200', 'Cancellation fee'),
    ('referral_bonus_cents', '500', 'Referral bonus amount'),
    ('max_dispatch_radius_m', '5000', 'Maximum dispatch radius in meters')
ON CONFLICT (key) DO NOTHING;