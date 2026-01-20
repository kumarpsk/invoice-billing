import { Navigate, Route, Routes } from 'react-router-dom';
import LoginScreen from './screens/LoginScreen';
import TerminalSelectScreen from './screens/TerminalSelectScreen';
import ShiftScreen from './screens/ShiftScreen';
import BillingScreen from './screens/BillingScreen';
import ReturnsScreen from './screens/ReturnsScreen';
import ReportsScreen from './screens/ReportsScreen';
import ProductsScreen from './screens/ProductsScreen';
import PrintPreview from './screens/PrintPreview';
import { isAuthenticated } from './auth';

export default function App() {
  return (
    <Routes>
      <Route path="/" element={isAuthenticated() ? <Navigate to="/terminal" /> : <Navigate to="/login" />} />
      <Route path="/login" element={<LoginScreen />} />
      <Route path="/terminal" element={<TerminalSelectScreen />} />
      <Route path="/shift" element={<ShiftScreen />} />
      <Route path="/billing" element={<BillingScreen />} />
      <Route path="/returns" element={<ReturnsScreen />} />
      <Route path="/reports" element={<ReportsScreen />} />
      <Route path="/products" element={<ProductsScreen />} />
      <Route path="/print" element={<PrintPreview />} />
    </Routes>
  );
}
