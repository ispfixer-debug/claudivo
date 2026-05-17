-- vito_mart - convenience store tables
-- Migration 0004 - PLAN.md §17
CREATE TABLE IF NOT EXISTS vito_mart_categories (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    name_es VARCHAR(100),
    description TEXT,
    description_es TEXT,
    display_order INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS vito_mart_products (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    category_id UUID REFERENCES vito_mart_categories(id) ON DELETE CASCADE,
    name VARCHAR(200) NOT NULL,
    name_es VARCHAR(200),
    description TEXT,
    description_es TEXT,
    price_cents BIGINT NOT NULL,
    image_url TEXT,
    stock INTEGER DEFAULT 100,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS vito_mart_orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    job_id UUID REFERENCES vito_jobs(id) ON DELETE SET NULL,
    client_id UUID REFERENCES vito_users(id) ON DELETE SET NULL,
    status VARCHAR(30) DEFAULT 'placed'
        CHECK (status IN ('placed', 'accepted', 'picking', 'packed', 'out_for_delivery', 'delivered', 'cancelled')),
    items JSONB NOT NULL,
    total_cents BIGINT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_vito_mart_products_category ON vito_mart_products(category_id);
CREATE INDEX idx_vito_mart_orders_client ON vito_mart_orders(client_id);
CREATE INDEX idx_vito_mart_orders_status ON vito_mart_orders(status);