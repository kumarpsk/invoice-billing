import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { apiFetch } from '../api';
import { getTerminal } from '../auth';

interface ShiftResponse {
  id: string;
  terminalCode: string;
  status: 'OPEN' | 'CLOSED';
  openingCash: number;
  closingCash?: number;
  openedAt: string;
}

export default function ShiftScreen() {
  const [shift, setShift] = useState<ShiftResponse | null>(null);
  const [openingCash, setOpeningCash] = useState('0');
  const [closingCash, setClosingCash] = useState('0');
  const [remarks, setRemarks] = useState('');
  const [error, setError] = useState('');
  const terminal = getTerminal();
  const navigate = useNavigate();

  useEffect(() => {
    if (!terminal) {
      navigate('/terminal');
      return;
    }
    apiFetch<ShiftResponse>(`/api/shifts/active?terminalCode=${terminal}`)
      .then(setShift)
      .catch(() => setShift(null));
  }, [terminal, navigate]);

  const openShift = async () => {
    if (!terminal) return;
    setError('');
    try {
      const data = await apiFetch<ShiftResponse>('/api/shifts/open', {
        method: 'POST',
        body: JSON.stringify({ terminalCode: terminal, openingCash: Number(openingCash) })
      });
      setShift(data);
    } catch (err) {
      setError((err as Error).message);
    }
  };

  const closeShift = async () => {
    if (!shift) return;
    setError('');
    try {
      await apiFetch<ShiftResponse>('/api/shifts/close', {
        method: 'POST',
        body: JSON.stringify({ shiftId: shift.id, closingCash: Number(closingCash), remarks })
      });
      setShift(null);
    } catch (err) {
      setError((err as Error).message);
    }
  };

  return (
    <div className="page">
      <header className="toolbar">
        <h2>Shift Management</h2>
        <button onClick={() => navigate('/billing')}>Go to Billing</button>
      </header>
      <div className="card">
        {shift ? (
          <div>
            <p>Active shift for {shift.terminalCode}</p>
            <p>Opening cash: ₹{shift.openingCash.toFixed(2)}</p>
            <label>
              Closing cash
              <input value={closingCash} onChange={(e) => setClosingCash(e.target.value)} />
            </label>
            <label>
              Remarks
              <input value={remarks} onChange={(e) => setRemarks(e.target.value)} />
            </label>
            <button className="danger" onClick={closeShift}>Close Shift</button>
          </div>
        ) : (
          <div>
            <p>No active shift.</p>
            <label>
              Opening cash
              <input value={openingCash} onChange={(e) => setOpeningCash(e.target.value)} />
            </label>
            <button className="primary" onClick={openShift}>Open Shift</button>
          </div>
        )}
        {error && <p className="error">{error}</p>}
      </div>
    </div>
  );
}
