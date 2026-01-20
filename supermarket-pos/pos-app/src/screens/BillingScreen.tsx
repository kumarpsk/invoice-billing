import { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { apiFetch } from '../api';
import { getTerminal } from '../auth';

interface Product {
  id: string;
  name: string;
  barcode: string;
  sellingPrice: number;
  taxInclusive: boolean;
  taxSlab: { rate: number };
}

interface CartItem {
  product: Product;
  qty: number;
  discountAmount: number;
}

export default function BillingScreen() {
  const [barcode, setBarcode] = useState('');
  const [cart, setCart] = useState<CartItem[]>([]);
  const [billDiscount, setBillDiscount] = useState(0);
  const [error, setError] = useState('');
  const [payMode, setPayMode] = useState('CASH');
  const [payAmount, setPayAmount] = useState('');
  const inputRef = useRef<HTMLInputElement>(null);
  const navigate = useNavigate();
  const terminal = getTerminal();

  useEffect(() => {
    inputRef.current?.focus();
  }, [cart]);

  const fetchProduct = async () => {
    if (!barcode) return;
    setError('');
    try {
      const products = await apiFetch<Product[]>(`/api/products?barcode=${barcode}`);
      if (!products.length) {
        setError('Product not found');
      } else {
        addToCart(products[0]);
      }
      setBarcode('');
    } catch (err) {
      setError((err as Error).message);
    }
  };

  const addToCart = (product: Product) => {
    setCart((prev) => {
      const existing = prev.find((item) => item.product.id === product.id);
      if (existing) {
        return prev.map((item) =>
          item.product.id === product.id ? { ...item, qty: item.qty + 1 } : item
        );
      }
      return [...prev, { product, qty: 1, discountAmount: 0 }];
    });
  };

  const updateQty = (id: string, qty: number) => {
    if (qty <= 0) {
      setCart((prev) => prev.filter((item) => item.product.id !== id));
      return;
    }
    setCart((prev) => prev.map((item) => (item.product.id === id ? { ...item, qty } : item)));
  };

  const updateDiscount = (id: string, discountAmount: number) => {
    setCart((prev) => prev.map((item) => (item.product.id === id ? { ...item, discountAmount } : item)));
  };

  const totals = cart.reduce(
    (acc, item) => {
      const lineBase = item.product.sellingPrice * item.qty;
      acc.subtotal += lineBase;
      acc.discount += item.discountAmount;
      return acc;
    },
    { subtotal: 0, discount: 0 }
  );
  const grandTotal = Math.max(0, totals.subtotal - totals.discount - billDiscount);

  const completeSale = async () => {
    if (!terminal) {
      setError('Terminal not set');
      return;
    }
    setError('');
    try {
      const payload = {
        terminalCode: terminal,
        billDiscountAmount: billDiscount,
        items: cart.map((item) => ({
          productId: item.product.id,
          qty: item.qty,
          discountAmount: item.discountAmount
        })),
        payments: [{ mode: payMode, amount: Number(payAmount || grandTotal) }]
      };
      const response = await apiFetch<{ invoiceNo: string; grandTotal: number }>(`/api/sales`, {
        method: 'POST',
        body: JSON.stringify(payload)
      });
      sessionStorage.setItem('print_payload', JSON.stringify({
        invoiceNo: response.invoiceNo,
        items: cart,
        totals: { ...totals, grandTotal: response.grandTotal }
      }));
      setCart([]);
      navigate('/print');
    } catch (err) {
      setError((err as Error).message);
    }
  };

  const holdSale = async () => {
    if (!terminal) {
      setError('Terminal not set');
      return;
    }
    try {
      await apiFetch('/api/sales/hold', {
        method: 'POST',
        body: JSON.stringify({
          terminalCode: terminal,
          billDiscountAmount: billDiscount,
          items: cart.map((item) => ({
            productId: item.product.id,
            qty: item.qty,
            discountAmount: item.discountAmount
          }))
        })
      });
      setCart([]);
    } catch (err) {
      setError((err as Error).message);
    }
  };

  const resumeHeld = async () => {
    if (!terminal) return;
    try {
      const held = await apiFetch<Array<{ id: string; invoiceNo: string }>>(`/api/sales/held?terminalCode=${terminal}`);
      if (!held.length) {
        setError('No held bills');
        return;
      }
      const details = await apiFetch<{ items: Array<{ product: Product; qty: number; discountAmount: number }> }>(
        `/api/sales/${held[0].id}`
      );
      setCart(details.items.map((item) => ({
        product: item.product,
        qty: item.qty,
        discountAmount: item.discountAmount
      })));
    } catch (err) {
      setError((err as Error).message);
    }
  };

  return (
    <div className="page">
      <header className="toolbar">
        <h2>Billing</h2>
        <div>
          <button onClick={() => navigate('/shift')}>Shift</button>
          <button onClick={() => navigate('/returns')}>Returns</button>
          <button onClick={() => navigate('/reports')}>Reports</button>
          <button onClick={() => navigate('/products')}>Stock</button>
        </div>
      </header>

      <div className="billing-layout">
        <div className="card">
          <label>
            Barcode
            <input
              ref={inputRef}
              value={barcode}
              onChange={(e) => setBarcode(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && fetchProduct()}
              placeholder="Scan barcode and press Enter"
            />
          </label>
          <table>
            <thead>
              <tr>
                <th>Item</th>
                <th>Qty</th>
                <th>Price</th>
                <th>Discount</th>
                <th>Total</th>
              </tr>
            </thead>
            <tbody>
              {cart.map((item) => (
                <tr key={item.product.id}>
                  <td>{item.product.name}</td>
                  <td>
                    <button onClick={() => updateQty(item.product.id, item.qty - 1)}>-</button>
                    <input
                      value={item.qty}
                      onChange={(e) => updateQty(item.product.id, Number(e.target.value))}
                    />
                    <button onClick={() => updateQty(item.product.id, item.qty + 1)}>+</button>
                  </td>
                  <td>₹{item.product.sellingPrice.toFixed(2)}</td>
                  <td>
                    <input
                      value={item.discountAmount}
                      onChange={(e) => updateDiscount(item.product.id, Number(e.target.value))}
                    />
                  </td>
                  <td>₹{(item.product.sellingPrice * item.qty - item.discountAmount).toFixed(2)}</td>
                </tr>
              ))}
            </tbody>
          </table>
          {error && <p className="error">{error}</p>}
        </div>

        <div className="card summary">
          <h3>Totals</h3>
          <p>Subtotal: ₹{totals.subtotal.toFixed(2)}</p>
          <p>Item Discount: ₹{totals.discount.toFixed(2)}</p>
          <label>
            Bill Discount
            <input value={billDiscount} onChange={(e) => setBillDiscount(Number(e.target.value))} />
          </label>
          <p className="grand">Grand Total: ₹{grandTotal.toFixed(2)}</p>
          <label>
            Payment Mode
            <select value={payMode} onChange={(e) => setPayMode(e.target.value)}>
              <option value="CASH">Cash</option>
              <option value="UPI">UPI</option>
              <option value="CARD">Card</option>
              <option value="SPLIT">Split</option>
            </select>
          </label>
          <label>
            Paid Amount
            <input value={payAmount} onChange={(e) => setPayAmount(e.target.value)} />
          </label>
          <button className="primary" onClick={completeSale}>Complete Sale</button>
          <button onClick={holdSale}>Hold Bill</button>
          <button onClick={resumeHeld}>Resume Held</button>
        </div>
      </div>
    </div>
  );
}
