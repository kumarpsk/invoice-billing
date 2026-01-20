import { useEffect, useState } from 'react';
import { apiFetch } from '../api';

interface StockRow {
  name: string;
  barcode: string;
  stock_qty: number;
}

export default function ProductsScreen() {
  const [query, setQuery] = useState('');
  const [stock, setStock] = useState<StockRow[]>([]);

  useEffect(() => {
    apiFetch<StockRow[]>('/api/stock').then(setStock).catch(() => setStock([]));
  }, []);

  const filtered = stock.filter((item) =>
    item.name.toLowerCase().includes(query.toLowerCase()) || (item.barcode || '').includes(query)
  );

  return (
    <div className="page">
      <div className="card">
        <h2>Products & Stock</h2>
        <input
          placeholder="Search by name or barcode"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
        />
        <table>
          <thead>
            <tr>
              <th>Name</th>
              <th>Barcode</th>
              <th>Stock</th>
            </tr>
          </thead>
          <tbody>
            {filtered.map((row) => (
              <tr key={row.name + row.barcode}>
                <td>{row.name}</td>
                <td>{row.barcode}</td>
                <td>{row.stock_qty}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
