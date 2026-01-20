import { useNavigate } from 'react-router-dom';
import { setTerminal } from '../auth';

const terminals = ['COUNTER-1', 'COUNTER-2'];

export default function TerminalSelectScreen() {
  const navigate = useNavigate();

  const handleSelect = (code: string) => {
    setTerminal(code);
    navigate('/shift');
  };

  return (
    <div className="page center">
      <div className="card">
        <h2>Select Terminal</h2>
        <div className="grid">
          {terminals.map((code) => (
            <button key={code} className="primary" onClick={() => handleSelect(code)}>
              {code}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
}
