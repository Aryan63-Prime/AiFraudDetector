#!/usr/bin/env bash
set -euo pipefail

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-'EOSQL'
    DO
    $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'transaction_user') THEN
            CREATE ROLE transaction_user LOGIN PASSWORD 'change_me';
        END IF;
        IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'alert_user') THEN
            CREATE ROLE alert_user LOGIN PASSWORD 'change_me';
        END IF;
    END
    $$;
EOSQL

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-'EOSQL'
    SELECT 'CREATE DATABASE transactions OWNER transaction_user'
    WHERE NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'transactions')\gexec
EOSQL

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-'EOSQL'
    SELECT 'CREATE DATABASE alerts OWNER alert_user'
    WHERE NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'alerts')\gexec
EOSQL
