-- Plans
CREATE TABLE IF NOT EXISTS plan (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    price_mad NUMERIC(10,2) NOT NULL,
    features JSONB NOT NULL DEFAULT '[]',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Subscriptions
CREATE TABLE IF NOT EXISTS subscription (
    id UUID PRIMARY KEY,
    doctor_id UUID NOT NULL,
    plan_id UUID NOT NULL REFERENCES plan(id),
    status VARCHAR(20) NOT NULL,
    start_at TIMESTAMPTZ NOT NULL,
    end_at TIMESTAMPTZ,
    renews_at TIMESTAMPTZ,
    trial_until TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_subscription_doctor ON subscription(doctor_id);

-- Invoices
CREATE TABLE IF NOT EXISTS invoice (
    id UUID PRIMARY KEY,
    subscription_id UUID NOT NULL REFERENCES subscription(id),
    amount_mad NUMERIC(10,2) NOT NULL,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    pdf_url TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Payment provider refs
CREATE TABLE IF NOT EXISTS payment_provider_ref (
    id UUID PRIMARY KEY,
    subscription_id UUID NOT NULL REFERENCES subscription(id),
    provider VARCHAR(20) NOT NULL,
    external_id VARCHAR(100),
    status VARCHAR(20),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
