CREATE TABLE tax_slabs (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name VARCHAR(20) NOT NULL,
  rate NUMERIC(5,2) NOT NULL
);

INSERT INTO tax_slabs (name, rate) VALUES
  ('GST_0', 0),
  ('GST_5', 5),
  ('GST_12', 12),
  ('GST_18', 18),
  ('GST_28', 28);

CREATE TABLE categories (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name VARCHAR(120) NOT NULL
);

CREATE TABLE vendors (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name VARCHAR(150) NOT NULL,
  phone VARCHAR(30),
  gstin VARCHAR(20)
);

CREATE TABLE products (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name VARCHAR(200) NOT NULL,
  barcode VARCHAR(50) UNIQUE,
  hsn_code VARCHAR(20),
  mrp NUMERIC(12,2) NOT NULL,
  selling_price NUMERIC(12,2) NOT NULL,
  cost_price NUMERIC(12,2) NOT NULL,
  is_tax_inclusive BOOLEAN NOT NULL DEFAULT true,
  tax_slab_id UUID REFERENCES tax_slabs(id),
  category_id UUID REFERENCES categories(id),
  vendor_id UUID REFERENCES vendors(id),
  created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE purchase_invoice (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  purchase_no VARCHAR(40) UNIQUE NOT NULL,
  vendor_id UUID REFERENCES vendors(id),
  invoice_date DATE NOT NULL,
  total_amount NUMERIC(12,2) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE purchase_item (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  purchase_id UUID REFERENCES purchase_invoice(id) ON DELETE CASCADE,
  product_id UUID REFERENCES products(id),
  qty NUMERIC(12,3) NOT NULL,
  cost_price NUMERIC(12,2) NOT NULL,
  line_total NUMERIC(12,2) NOT NULL
);

CREATE TYPE inventory_txn_type AS ENUM ('PURCHASE_IN', 'SALE_OUT', 'RETURN_IN', 'ADJUSTMENT', 'OPENING_STOCK');

CREATE TABLE inventory_movement (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  product_id UUID REFERENCES products(id),
  txn_type inventory_txn_type NOT NULL,
  qty_in NUMERIC(12,3) NOT NULL DEFAULT 0,
  qty_out NUMERIC(12,3) NOT NULL DEFAULT 0,
  ref_no VARCHAR(50),
  created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE OR REPLACE VIEW v_product_stock AS
SELECT product_id,
       SUM(qty_in - qty_out) AS stock_qty
FROM inventory_movement
GROUP BY product_id;
