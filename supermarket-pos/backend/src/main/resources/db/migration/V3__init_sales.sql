CREATE TYPE invoice_status AS ENUM ('DRAFT', 'HELD', 'COMPLETED', 'CANCELLED');

CREATE TABLE sales_invoice (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  invoice_no VARCHAR(40) UNIQUE NOT NULL,
  terminal_code VARCHAR(30) NOT NULL,
  shift_id UUID REFERENCES cash_register_shift(id),
  status invoice_status NOT NULL DEFAULT 'COMPLETED',
  bill_datetime TIMESTAMP NOT NULL DEFAULT now(),
  customer_name VARCHAR(150),
  customer_phone VARCHAR(20),
  subtotal NUMERIC(12,2) NOT NULL,
  discount_total NUMERIC(12,2) NOT NULL,
  taxable_total NUMERIC(12,2) NOT NULL,
  cgst_total NUMERIC(12,2) NOT NULL,
  sgst_total NUMERIC(12,2) NOT NULL,
  round_off NUMERIC(12,2) NOT NULL,
  grand_total NUMERIC(12,2) NOT NULL
);

CREATE TABLE sales_item (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  sales_id UUID REFERENCES sales_invoice(id) ON DELETE CASCADE,
  product_id UUID REFERENCES products(id),
  qty NUMERIC(12,3) NOT NULL,
  price NUMERIC(12,2) NOT NULL,
  discount_amount NUMERIC(12,2) NOT NULL,
  taxable_amount NUMERIC(12,2) NOT NULL,
  cgst_amount NUMERIC(12,2) NOT NULL,
  sgst_amount NUMERIC(12,2) NOT NULL,
  line_total NUMERIC(12,2) NOT NULL
);

CREATE TABLE sales_payment (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  sales_id UUID REFERENCES sales_invoice(id) ON DELETE CASCADE,
  mode payment_mode NOT NULL,
  amount NUMERIC(12,2) NOT NULL,
  ref_no VARCHAR(50)
);

CREATE INDEX idx_sales_bill_datetime ON sales_invoice (bill_datetime);
CREATE INDEX idx_sales_shift_id ON sales_invoice (shift_id);
CREATE INDEX idx_sales_item_sales_id ON sales_item (sales_id);
