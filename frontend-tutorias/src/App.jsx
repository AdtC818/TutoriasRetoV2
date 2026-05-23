import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import PrivateRoute from './components/PrivateRoute';
import Login from './pages/Login';
import Register from './pages/Register';
import DashboardEstudiante from './pages/DashboardEstudiante';
import ReservasEstudiante from './pages/ReservasEstudiante';
import Historial from './pages/Historial';
import PanelTutor from './pages/PanelTutor';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Navigate to="/login" />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/dashboard" element={<PrivateRoute><DashboardEstudiante /></PrivateRoute>} />
        <Route path="/reservas" element={<PrivateRoute><ReservasEstudiante /></PrivateRoute>} />
        <Route path="/historial" element={<PrivateRoute><Historial /></PrivateRoute>} />
        <Route path="/tutor" element={<PrivateRoute><PanelTutor /></PrivateRoute>} />
      </Routes>
    </Router>
  );
}

export default App;
