import { useEffect, useState } from 'react';
import { apiFetch } from '../api';

export default function ReportsScreen() {
  const [data, setData] = useState<Array<{ sales_date: string; total_sales: number }>>([]);

  useEffect(() => {
    const today = new Date().toISOString().slice(0, 10);
    apiFetch(`/api/reports/daily-sales?from=${today}&to=${today}`)
      .then(setData)
      .catch(() => setData([]));
  }, []);

  return (
    <div className="page">
      <div className="card">
        <h2>Daily Sales</h2>
        <table>
          <thead>
            <tr>
              <th>Date</th>
              <th>Total Sales</th>
            </tr>
          </thead>
          <tbody>
            {data.map((row) => (
              <tr key={row.sales_date}>
                <td>{row.sales_date}</td>
                <td>₹{Number(row.total_sales || 0).toFixed(2)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
