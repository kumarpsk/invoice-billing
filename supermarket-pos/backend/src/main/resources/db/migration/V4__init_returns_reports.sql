CREATE TABLE sales_return (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  return_no VARCHAR(40) UNIQUE NOT NULL,
  sales_id UUID REFERENCES sales_invoice(id),
  refund_mode payment_mode NOT NULL,
  refund_ref VARCHAR(50),
  total_amount NUMERIC(12,2) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE sales_return_item (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  return_id UUID REFERENCES sales_return(id) ON DELETE CASCADE,
  product_id UUID REFERENCES products(id),
  qty NUMERIC(12,3) NOT NULL,
  amount NUMERIC(12,2) NOT NULL
);

CREATE OR REPLACE VIEW v_daily_sales AS
SELECT (bill_datetime AT TIME ZONE 'Asia/Kolkata')::date AS sales_date,
       SUM(grand_total) AS total_sales
FROM sales_invoice
WHERE status = 'COMPLETED'
GROUP BY (bill_datetime AT TIME ZONE 'Asia/Kolkata')::date;
