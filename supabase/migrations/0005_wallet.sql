-- vito_wallets and vito_transactions
-- Migration 0005 - PLAN.md §15.2
CREATE TABLE IF NOT EXISTS vito_wallets (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID UNIQUE REFERENCES vito_users(id) ON DELETE CASCADE,
    balance_cents BIGINT DEFAULT 0,
    stripe_customer_id TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS vito_transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    wallet_id UUID REFERENCES vito_wallets(id) ON DELETE CASCADE,
    type VARCHAR(30) NOT NULL CHECK (type IN (
        'top_up', 'ride_fare', 'ride_fare_refund', 'delivery_fare', 
        'delivery_fare_refund', 'mart_order', 'mart_order_refund', 
        'cancellation_fee', 'payout', 'bonus', 'adjustment'
    )),
    amount_cents BIGINT NOT NULL,
    job_id UUID REFERENCES vito_jobs(id),
    payout_request_id UUID,
    stripe_payment_intent_id TEXT,
    description TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_vito_transactions_wallet ON vito_transactions(wallet_id);
CREATE INDEX idx_vito_transactions_type ON vito_transactions(type);

-- vito_payout_requests
CREATE TABLE IF NOT EXISTS vito_payout_requests (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    driver_id UUID REFERENCES vito_users(id) ON DELETE CASCADE,
    amount_cents BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'pending'
        CHECK (status IN ('pending', 'approved', 'rejected', 'paid', 'failed')),
    stripe_transfer_id TEXT,
    admin_note TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    processed_at TIMESTAMPTZ
);

CREATE INDEX idx_vito_payout_driver ON vito_payout_requests(driver_id);
CREATE INDEX idx_vito_payout_status ON vito_payout_requests(status) WHERE status = 'pending';