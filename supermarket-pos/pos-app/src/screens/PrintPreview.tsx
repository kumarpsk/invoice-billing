import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

export default function PrintPreview() {
  const navigate = useNavigate();
  const payload = sessionStorage.getItem('print_payload');
  const data = payload ? JSON.parse(payload) : null;

  useEffect(() => {
    if (data) {
      setTimeout(() => window.print(), 500);
    }
  }, [data]);

  if (!data) {
    return (
      <div className="page">
        <p>No receipt data.</p>
        <button onClick={() => navigate('/billing')}>Back to Billing</button>
      </div>
    );
  }

  return (
    <div className="receipt">
      <h3>Supermarket POS</h3>
      <p>Tax Invoice</p>
      <p>Invoice: {data.invoiceNo}</p>
      <hr />
      <table>
        <thead>
          <tr>
            <th>Item</th>
            <th>Qty</th>
            <th>Amt</th>
          </tr>
        </thead>
        <tbody>
          {data.items.map((item: any) => (
            <tr key={item.product.id}>
              <td>{item.product.name}</td>
              <td>{item.qty}</td>
              <td>₹{(item.product.sellingPrice * item.qty - item.discountAmount).toFixed(2)}</td>
            </tr>
          ))}
        </tbody>
      </table>
      <hr />
      <p>Subtotal: ₹{data.totals.subtotal.toFixed(2)}</p>
      <p>Discount: ₹{data.totals.discount.toFixed(2)}</p>
      <p className="grand">Grand Total: ₹{data.totals.grandTotal.toFixed(2)}</p>
      <p>Thank you! Visit again.</p>
      <button className="no-print" onClick={() => navigate('/billing')}>Back</button>
    </div>
  );
}
