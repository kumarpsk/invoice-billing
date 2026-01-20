import { useState } from 'react';
import { apiFetch } from '../api';

interface SalesItem {
  product: { id: string; name: string };
  qty: number;
}

export default function ReturnsScreen() {
  const [invoiceNo, setInvoiceNo] = useState('');
  const [items, setItems] = useState<SalesItem[]>([]);
  const [returnQty, setReturnQty] = useState<Record<string, number>>({});
  const [refundMode, setRefundMode] = useState('CASH');
  const [message, setMessage] = useState('');

  const lookupInvoice = async () => {
    try {
      const data = await apiFetch<{ items: SalesItem[] }>(`/api/sales/by-invoice?invoiceNo=${invoiceNo}`);
      setItems(data.items);
      setMessage('');
    } catch (err) {
      setMessage((err as Error).message);
    }
  };

  const submitReturn = async () => {
    try {
      await apiFetch('/api/returns', {
        method: 'POST',
        body: JSON.stringify({
          invoiceNo,
          refundMode,
          items: items.map((item) => ({
            productId: item.product.id,
            qty: returnQty[item.product.id] || 0
          }))
        })
      });
      setMessage('Return processed.');
      setItems([]);
    } catch (err) {
      setMessage((err as Error).message);
    }
  };

  return (
    <div className="page">
      <div className="card">
        <h2>Returns</h2>
        <label>
          Invoice No
          <input value={invoiceNo} onChange={(e) => setInvoiceNo(e.target.value)} />
        </label>
        <button onClick={lookupInvoice}>Lookup</button>
        {items.length > 0 && (
          <div>
            <table>
              <thead>
                <tr>
                  <th>Item</th>
                  <th>Sold Qty</th>
                  <th>Return Qty</th>
                </tr>
              </thead>
              <tbody>
                {items.map((item) => (
                  <tr key={item.product.id}>
                    <td>{item.product.name}</td>
                    <td>{item.qty}</td>
                    <td>
                      <input
                        value={returnQty[item.product.id] || ''}
                        onChange={(e) =>
                          setReturnQty((prev) => ({
                            ...prev,
                            [item.product.id]: Number(e.target.value)
                          }))
                        }
                      />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            <label>
              Refund Mode
              <select value={refundMode} onChange={(e) => setRefundMode(e.target.value)}>
                <option value="CASH">Cash</option>
                <option value="UPI">UPI</option>
                <option value="CARD">Card</option>
              </select>
            </label>
            <button className="primary" onClick={submitReturn}>Submit Return</button>
          </div>
        )}
        {message && <p className="info">{message}</p>}
      </div>
    </div>
  );
}
