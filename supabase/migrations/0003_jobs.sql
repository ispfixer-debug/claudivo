-- vito_jobs - core job table
-- Migration 0003 - PLAN.md §6.3
CREATE TABLE IF NOT EXISTS vito_jobs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    client_id UUID REFERENCES vito_users(id) ON DELETE SET NULL,
    driver_id UUID REFERENCES vito_users(id) ON DELETE SET NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('ride', 'send', 'mart')),
    status VARCHAR(20) NOT NULL DEFAULT 'requested' 
        CHECK (status IN ('requested', 'searching', 'accepted', 'en_route', 'arrived', 'in_progress', 'completed', 'cancelled')),
    pickup_address TEXT NOT NULL,
    pickup_lat DOUBLE PRECISION NOT NULL,
    pickup_lng DOUBLE PRECISION NOT NULL,
    destination_address TEXT,
    destination_lat DOUBLE PRECISION,
    destination_lng DOUBLE PRECISION,
    fare_cents BIGINT NOT NULL,
    distance_m INTEGER DEFAULT 0,
    estimated_duration_m INTEGER DEFAULT 0,
    rating INTEGER CHECK (rating >= 1 AND rating <= 5),
    rating_comment TEXT,
    cancellation_fee_cents BIGINT,
    package_description TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    completed_at TIMESTAMPTZ
);

CREATE INDEX idx_vito_jobs_client ON vito_jobs(client_id);
CREATE INDEX idx_vito_jobs_driver ON vito_jobs(driver_id);
CREATE INDEX idx_vito_jobs_status ON vito_jobs(status);

-- vito_dispatch_offers - offers sent to drivers
CREATE TABLE IF NOT EXISTS vito_dispatch_offers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    job_id UUID REFERENCES vito_jobs(id) ON DELETE CASCADE,
    driver_id UUID REFERENCES vito_users(id) ON DELETE CASCADE,
    status VARCHAR(20) DEFAULT 'pending'
        CHECK (status IN ('pending', 'accepted', 'declined', 'expired', 'cancelled')),
    expires_at TIMESTAMPTZ NOT NULL,
    fare_cents BIGINT NOT NULL,
    distance_from_driver_m INTEGER,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_vito_dispatch_job ON vito_dispatch_offers(job_id);
CREATE INDEX idx_vito_dispatch_driver ON vito_dispatch_offers(driver_id);
CREATE INDEX idx_vito_dispatch_status ON vito_dispatch_offers(status) WHERE status = 'pending';