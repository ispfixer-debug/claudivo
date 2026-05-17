-- QR tokens for onboarding and referrals
-- Migration 0006 - PLAN.md §18
CREATE TABLE IF NOT EXISTS vito_qr_tokens (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    token VARCHAR(64) UNIQUE NOT NULL,
    kind VARCHAR(20) NOT NULL CHECK (kind IN ('onboarding', 'referral')),
    created_by_user_id UUID REFERENCES vito_users(id),
    target_user_id UUID,
    is_active BOOLEAN DEFAULT true,
    expires_at TIMESTAMPTZ NOT NULL,
    redeemed_at TIMESTAMPTZ,
    scan_count INTEGER DEFAULT 0,
    max_scans INTEGER DEFAULT 1,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_vito_qr_token ON vito_qr_tokens(token);
CREATE INDEX idx_vito_qr_kind ON vito_qr_tokens(kind);
CREATE INDEX idx_vito_qr_active ON vito_qr_tokens(is_active) WHERE is_active = true;

-- Referrals tracking
CREATE TABLE IF NOT EXISTS vito_referrals (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    referrer_user_id UUID REFERENCES vito_users(id),
    referred_user_id UUID REFERENCES vito_users(id),
    referral_code VARCHAR(20) UNIQUE,
    bonus_amount_cents BIGINT DEFAULT 500,
    status VARCHAR(20) DEFAULT 'pending'
        CHECK (status IN ('pending', 'claimed', 'bonus_applied', 'expired')),
    claimed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_vito_referrals_referrer ON vito_referrals(referrer_user_id);
CREATE INDEX idx_vito_referrals_code ON vito_referrals(referral_code);