CREATE TABLE IF NOT EXISTS fraud_alerts (
    id UUID PRIMARY KEY,
    transaction_id VARCHAR(64) NOT NULL UNIQUE,
    risk_score DOUBLE PRECISION NOT NULL,
    risk_level VARCHAR(16) NOT NULL,
    recommendation VARCHAR(16) NOT NULL,
    evaluated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(16) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_fraud_alerts_status
    ON fraud_alerts (status);
