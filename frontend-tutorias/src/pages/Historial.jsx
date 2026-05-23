import React, { useEffect, useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { commandClient } from '../api/commands';
import { queryClient } from '../api/queries';
import Swal from 'sweetalert2';
import moment from 'moment';
import './Historial.css';
import './Dashboard.css'; // Para reusar el navbar

export default function Historial() {
  const [historial, setHistorial] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    cargarHistorial();
  }, []);

  const cargarHistorial = async () => {
    try {
      // 1. Obtener usuario
      const meResponse = await queryClient.get('/auth/me');
      const userId = meResponse.data.id;

      // 2. Obtener historial
      const histResponse = await queryClient.get(`/api/reservas/estudiante/${userId}/historial`);
      setHistorial(histResponse.data);
    } catch (error) {
      console.error(error);
      Swal.fire('Error', 'No se pudo cargar el historial', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  return (
    <div className="dashboard-container">
      {/* Mismo Navbar que Dashboard */}
      <nav className="dashboard-nav">
        <div className="nav-logo">Tutorías</div>
        <div className="nav-links">
          <Link to="/dashboard" className="nav-link">Nueva Reserva</Link>
          <Link to="/reservas" className="nav-link">Mis Reservas Activas</Link>
          <button onClick={handleLogout} className="logout-btn">
            Cerrar sesión
          </button>
        </div>
      </nav>

      <div className="dashboard-content">
        <h1 className="dashboard-title">Mi Historial de Tutorías</h1>
        <p className="dashboard-subtitle">Revisa tus tutorías pasadas</p>

        {loading ? (
          <p>Cargando historial...</p>
        ) : historial.length === 0 ? (
          <div className="empty-historial">
            <p>No tienes tutorías pasadas aún.</p>
            <button className="volver-btn" onClick={() => navigate('/dashboard')}>
              Volver al inicio
            </button>
          </div>
        ) : (
          <div className="historial-grid">
            {historial.map((res) => (
              <div key={res.id} className="historial-card">
                <h3>{res.materiaNombre || 'Tutoría'}</h3>
                <p><strong>Tutor:</strong> {res.tutorNombre}</p>
                <p><strong>Fecha:</strong> {moment(res.fecha).format('DD/MM/YYYY')}</p>
                <p><strong>Hora:</strong> {res.horaInicio} - {res.horaFin}</p>
                
                <span className={`estado-badge ${res.estado.toLowerCase()}`}>
                  {res.estado}
                </span>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}