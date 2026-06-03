import React, { useEffect, useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { queryClient } from '../api/queries';
import Swal from 'sweetalert2';
import moment from 'moment';
import './Dashboard.css';

export default function Historial() {
  const [historial, setHistorial] = useState([]);
  const [loading, setLoading] = useState(true);
  const [user, setUser] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    cargarHistorial();
  }, []);

  const cargarHistorial = async () => {
    try {
      const meResponse = await queryClient.get('/auth/me');
      const userData = meResponse.data;
      setUser(userData);
      const userId = userData.correo;

      // Cargar materias para mapear ID → nombre
      let materiasDict = {};
      try {
        const matRes = await queryClient.get('/api/materias/');
        matRes.data.forEach(m => materiasDict[m.id] = m.nombre);
      } catch(e) {}

      const histResponse = await queryClient.get(`/api/reservas/estudiante/${userId}/historial`);
      const lista = histResponse.data.data || histResponse.data || [];
      const arr = Array.isArray(lista) ? lista : [];

      const enriquecido = arr.map(r => ({
        ...r,
        materiaNombre: materiasDict[r.materiaId] || `Materia #${r.materiaId}`
      }));

      setHistorial(enriquecido);
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

  const estadoColor = (estado) => {
    const map = {
      COMPLETADA: { backgroundColor: '#e8f5e9', color: '#2e7d32' },
      INASISTENCIA: { backgroundColor: '#ffebee', color: '#c62828' },
      CANCELADA: { backgroundColor: '#fff3e0', color: '#e65100' },
      ACTIVA: { backgroundColor: '#e3f2fd', color: '#1565c0' },
    };
    return map[estado] || {};
  };

  return (
    <div className='dashboard-container'>
      <nav className='navbar'>
        <div className='navbar-brand'>🎓 Mi Historial</div>
        <div className='navbar-links'>
          {user && (
            <div className='user-profile'>
              <div className='user-info'>
                <span className='user-name'>{user.nombre}</span>
                <span className='user-details'>{user.rol} | {user.carrera} - Semestre {user.semestre}</span>
              </div>
              <div style={{width:'35px',height:'35px',borderRadius:'50%',backgroundColor:'#fff',color:'#1877f2',display:'flex',justifyContent:'center',alignItems:'center',fontWeight:'bold',fontSize:'1.2rem'}}>
                {user.correo?.charAt(0).toUpperCase()}
              </div>
            </div>
          )}
          <Link to='/dashboard' className='nav-link'>Ver Catálogo</Link>
          <Link to='/reservas' className='nav-link'>Mis Reservas</Link>
          <button onClick={handleLogout} className='btn-logout'>Cerrar Sesión</button>
        </div>
      </nav>

      <div className='content-wrapper'>
        <h1 className='page-title'>Mi Historial de Tutorías</h1>

        {loading ? (
          <p style={{textAlign:'center', fontSize:'1.1rem', color:'#666', padding:'50px 0'}}>
            Cargando historial...
          </p>
        ) : historial.length === 0 ? (
          <div style={{textAlign:'center', padding:'3rem', background:'white', borderRadius:'12px', boxShadow:'0 4px 15px rgba(0,0,0,0.05)', marginTop:'2rem'}}>
            <p style={{fontSize:'1.1rem', color:'#666', marginBottom:'1.5rem'}}>
              No tienes tutorías pasadas aún.
            </p>
            <button className='btn-primary' style={{width:'auto', padding:'0.8rem 1.5rem', margin:0}} onClick={() => navigate('/dashboard')}>
              Ir al Catálogo
            </button>
          </div>
        ) : (
          <div className='cards-grid'>
            {historial.map((res) => (
              <div key={res.id} className='card'>
                <div>
                  <span className='card-badge'>ID Reserva: #{res.id}</span>
                  <h3 className='card-title'>{res.materiaNombre}</h3>
                  <p className='card-text'><strong>Tutor:</strong> {res.tutorId}</p>
                  <p className='card-text'><strong>Fecha:</strong> {moment(res.fechaSesion).format('DD/MM/YYYY')}</p>
                </div>
                <span style={{
                  display: 'inline-block',
                  padding: '0.4rem 0.8rem',
                  borderRadius: '20px',
                  fontSize: '0.85rem',
                  fontWeight: '600',
                  marginTop: '1rem',
                  alignSelf: 'flex-start',
                  ...estadoColor(res.estado)
                }}>
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