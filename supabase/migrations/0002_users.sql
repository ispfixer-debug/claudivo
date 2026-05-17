-- vito_users - unified user table
-- Migration 0002 - PLAN.md §6.2
CREATE TABLE IF NOT EXISTS vito_users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    phone VARCHAR(20) UNIQUE NOT NULL,
    display_name VARCHAR(100),
    role VARCHAR(20) NOT NULL CHECK (role IN ('client', 'driver', 'admin', 'super_admin')),
    language VARCHAR(5) DEFAULT 'en',
    fcm_token TEXT,
    pin_hash TEXT, -- Local PIN for quick auth
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Index on phone for lookups
CREATE INDEX idx_vito_users_phone ON vito_users(phone);
CREATE INDEX idx_vito_users_role ON vito_users(role);

-- vito_clients - client-specific data
CREATE TABLE IF NOT EXISTS vito_clients (
    user_id UUID PRIMARY KEY REFERENCES vito_users(id) ON DELETE CASCADE,
    email VARCHAR(255),
    rating DECIMAL(3,2) DEFAULT 0,
    rating_count INTEGER DEFAULT 0,
    wallet_balance_cents BIGINT DEFAULT 0,
    referrer_code VARCHAR(20) UNIQUE,
    referred_by_code VARCHAR(20),
    total_trips INTEGER DEFAULT 0
);

-- vito_drivers - driver-specific data
CREATE TABLE IF NOT EXISTS vito_drivers (
    user_id UUID PRIMARY KEY REFERENCES vito_users(id) ON DELETE CASCADE,
    rating DECIMAL(3,2) DEFAULT 0,
    rating_count INTEGER DEFAULT 0,
    wallet_balance_cents BIGINT DEFAULT 0,
    car_make VARCHAR(50),
    car_model VARCHAR(50),
    car_color VARCHAR(30),
    car_plate VARCHAR(20),
    car_year INTEGER,
    car_photo_url TEXT,
    kyc_status VARCHAR(20) DEFAULT 'not_started' CHECK (kyc_status IN ('not_started', 'pending', 'approved', 'rejected', 'expired')),
    stripe_account_id TEXT,
    stripe_onboarding_complete BOOLEAN DEFAULT false,
    is_online BOOLEAN DEFAULT false,
    location GEOGRAPHY(Point, 4326),
    driver_referral_code VARCHAR(20) UNIQUE
);

-- Index for location-based queries
CREATE INDEX idx_vito_drivers_location ON vito_drivers USING GIST(location);
CREATE INDEX idx_vito_drivers_online ON vito_drivers(is_online) WHERE is_online = true;

-- vito_admins - admin-specific data
CREATE TABLE IF NOT EXISTS vito_admins (
    user_id UUID PRIMARY KEY REFERENCES vito_users(id) ON DELETE CASCADE,
    can_manage_drivers BOOLEAN DEFAULT false,
    can_manage_clients BOOLEAN DEFAULT false,
    can_view_finance BOOLEAN DEFAULT false,
    can_manage_feature_flags BOOLEAN DEFAULT false,
    can_manage_admins BOOLEAN DEFAULT false
);

-- vito_kyc_documents - KYC documents for drivers
CREATE TABLE IF NOT EXISTS vito_kyc_documents (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    driver_id UUID REFERENCES vito_users(id) ON DELETE CASCADE,
    doc_type VARCHAR(30) CHECK (doc_type IN ('government_id', 'drivers_license', 'vehicle_insurance')),
    image_url TEXT,
    status VARCHAR(20) DEFAULT 'pending' CHECK (status IN ('pending', 'approved', 'rejected')),
    rejection_reason TEXT,
    submitted_at TIMESTAMPTZ,
    reviewed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_vito_kyc_driver ON vito_kyc_documents(driver_id);