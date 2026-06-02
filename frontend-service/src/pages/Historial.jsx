import React, { useEffect, useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { queryClient } from '../api/queries';
import Swal from 'sweetalert2';
import moment from 'moment';
import './Historial.css';
import './Dashboard.css';

export default function Historial() {
  const [historial, setHistorial] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    cargarHistorial();
  }, []);

  const cargarHistorial = async () => {
    try {
      const meResponse = await queryClient.get('/auth/me');
      const userId = meResponse.data.correo; // el backend usa correo como estudianteId

      const histResponse = await queryClient.get(`/api/reservas/estudiante/${userId}/historial`);
      // La respuesta es { success, mensaje, data: [...] }
      const lista = histResponse.data.data || histResponse.data || [];
      setHistorial(Array.isArray(lista) ? lista : []);
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

  const estadoLabel = (estado) => {
    const map = {
      COMPLETADA: '✅ Completada',
      INASISTENCIA: '❌ Inasistencia',
      CANCELADA: '🚫 Cancelada',
      ACTIVA: '🟢 Activa',
    };
    return map[estado] || estado;
  };

  return (
    <div className="dashboard-container">
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
                <h3>Tutoría #{res.id}</h3>
                <p><strong>Materia ID:</strong> {res.materiaId}</p>
                <p><strong>Tutor:</strong> {res.tutorId}</p>
                <p><strong>Fecha:</strong> {moment(res.fechaSesion).format('DD/MM/YYYY')}</p>
                <span className={`estado-badge ${res.estado?.toLowerCase()}`}>
                  {estadoLabel(res.estado)}
                </span>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}