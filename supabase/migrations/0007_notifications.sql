-- Notifications
-- Migration 0007 - PLAN.md §20
CREATE TABLE IF NOT EXISTS vito_notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES vito_users(id) ON DELETE CASCADE,
    type VARCHAR(30) NOT NULL CHECK (type IN (
        'job_offer', 'job_accepted', 'job_started', 'job_completed', 
        'job_cancelled', 'kyc_approved', 'kyc_rejected', 
        'payout_approved', 'payout_rejected', 'referral_bonus', 'system'
    )),
    title TEXT NOT NULL,
    body TEXT NOT NULL,
    data JSONB,
    is_read BOOLEAN DEFAULT false,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_vito_notifications_user ON vito_notifications(user_id);
CREATE INDEX idx_vito_notifications_read ON vito_notifications(user_id, is_read);

-- Feature flags
-- Migration 0008 - PLAN.md §8.11
CREATE TABLE IF NOT EXISTS vito_feature_flags (
    key VARCHAR(50) PRIMARY KEY,
    value TEXT NOT NULL,
    description TEXT,
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Audit log
-- Migration 0009 - PLAN.md §21.1
CREATE TABLE IF NOT EXISTS vito_audit_log (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    action VARCHAR(100) NOT NULL,
    actor_type VARCHAR(20) NOT NULL CHECK (actor_type IN ('client', 'driver', 'admin', 'system', 'dispatch')),
    actor_id UUID NOT NULL,
    target_type VARCHAR(50),
    target_id UUID,
    metadata JSONB,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_vito_audit_action ON vito_audit_log(action);
CREATE INDEX idx_vito_audit_actor ON vito_audit_log(actor_id);
CREATE INDEX idx_vito_audit_created ON vito_audit_log(created_at DESC);