CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TYPE payment_mode AS ENUM ('CASH', 'UPI', 'CARD', 'SPLIT');
CREATE TYPE user_status AS ENUM ('ACTIVE', 'INACTIVE');
CREATE TYPE shift_status AS ENUM ('OPEN', 'CLOSED');

CREATE TABLE roles (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE users (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  username VARCHAR(100) UNIQUE NOT NULL,
  password_hash VARCHAR(200) NOT NULL,
  full_name VARCHAR(150) NOT NULL,
  status user_status NOT NULL DEFAULT 'ACTIVE',
  role_id UUID REFERENCES roles(id),
  created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE store_settings (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  store_name VARCHAR(200) NOT NULL,
  address_line1 VARCHAR(200),
  address_line2 VARCHAR(200),
  gstin VARCHAR(20),
  phone VARCHAR(20)
);

CREATE TABLE document_sequence (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  doc_type VARCHAR(50) NOT NULL,
  fiscal_year VARCHAR(10) NOT NULL,
  next_number BIGINT NOT NULL DEFAULT 1,
  UNIQUE (doc_type, fiscal_year)
);

CREATE TABLE audit_log (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  event_type VARCHAR(50) NOT NULL,
  event_message TEXT,
  created_by VARCHAR(100),
  created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE cash_register_shift (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  terminal_code VARCHAR(30) NOT NULL,
  status shift_status NOT NULL DEFAULT 'OPEN',
  opening_cash NUMERIC(12,2) NOT NULL,
  closing_cash NUMERIC(12,2),
  remarks TEXT,
  opened_at TIMESTAMP NOT NULL DEFAULT now(),
  closed_at TIMESTAMP
);

INSERT INTO roles (name) VALUES ('ADMIN'), ('CASHIER'), ('MANAGER');
