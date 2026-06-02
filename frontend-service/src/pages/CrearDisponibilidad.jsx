import React, { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Swal from 'sweetalert2';
import { commandClient } from '../api/commands';
import { queryClient } from '../api/queries';
import './Dashboard.css';

const toApiDateTime = (value) => value ? value.replace('T', ' ') + ':00' : '';

export default function CrearDisponibilidad() {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [materias, setMaterias] = useState([]);
  const [form, setForm] = useState({
    materia_id: '',
    fecha_inicio: '',
    fecha_fin: ''
  });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const loadData = async () => {
      try {
        const userRes = await queryClient.get('/auth/me');
        if (userRes.data.rol !== 'TUTOR') {
          navigate('/dashboard');
          return;
        }

        setUser(userRes.data);
        const materiasRes = await queryClient.get('/api/materias/');
        setMaterias(materiasRes.data || []);
      } catch (error) {
        if (error.response?.status === 401) navigate('/login');
        Swal.fire('Error', 'No se pudo cargar la informacion inicial.', 'error');
      }
    };

    loadData();
  }, [navigate]);

  const canSubmit = useMemo(() => (
    user &&
    form.materia_id &&
    form.fecha_inicio &&
    form.fecha_fin &&
    new Date(form.fecha_fin) > new Date(form.fecha_inicio)
  ), [form, user]);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (!canSubmit) {
      Swal.fire('Revisa los datos', 'La materia y el rango de fechas son obligatorios.', 'warning');
      return;
    }

    setLoading(true);
    try {
      await commandClient.post('/api/bloques', {
        tutor_id: user.correo,
        materia_id: Number(form.materia_id),
        fecha_inicio: toApiDateTime(form.fecha_inicio),
        fecha_fin: toApiDateTime(form.fecha_fin)
      });

      await Swal.fire('Disponibilidad creada', 'El bloque quedo publicado para reservas.', 'success');
      navigate('/tutor');
    } catch (error) {
      Swal.fire(
        'Error',
        error.response?.data?.error || error.message || 'No se pudo crear la disponibilidad.',
        'error'
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className='dashboard-container'>
      <nav className='navbar'>
        <div className='navbar-brand'>Panel del Tutor</div>
        <div className='navbar-links'>
          <button onClick={() => navigate('/tutor')} className='btn-logout'>Volver</button>
        </div>
      </nav>

      <div className='content-wrapper'>
        <h1 className='page-title'>Crear disponibilidad</h1>

        <form
          onSubmit={handleSubmit}
          style={{
            maxWidth: '680px',
            backgroundColor: 'rgba(255,255,255,0.9)',
            borderRadius: '12px',
            padding: '24px',
            boxShadow: '0 8px 32px rgba(31,38,135,0.08)'
          }}
        >
          <label className='card-text' htmlFor='materia_id'><strong>Materia</strong></label>
          <select
            id='materia_id'
            name='materia_id'
            value={form.materia_id}
            onChange={handleChange}
            style={{width: '100%', padding: '12px', margin: '8px 0 18px', borderRadius: '8px', border: '1px solid #cbd5e1'}}
          >
            <option value=''>Selecciona una materia</option>
            {materias.map((materia) => (
              <option key={materia.id} value={materia.id}>{materia.nombre}</option>
            ))}
          </select>

          <label className='card-text' htmlFor='fecha_inicio'><strong>Inicio</strong></label>
          <input
            id='fecha_inicio'
            type='datetime-local'
            name='fecha_inicio'
            value={form.fecha_inicio}
            onChange={handleChange}
            style={{width: '100%', padding: '12px', margin: '8px 0 18px', borderRadius: '8px', border: '1px solid #cbd5e1'}}
          />

          <label className='card-text' htmlFor='fecha_fin'><strong>Fin</strong></label>
          <input
            id='fecha_fin'
            type='datetime-local'
            name='fecha_fin'
            value={form.fecha_fin}
            onChange={handleChange}
            style={{width: '100%', padding: '12px', margin: '8px 0 24px', borderRadius: '8px', border: '1px solid #cbd5e1'}}
          />

          <button
            className='btn-primary'
            type='submit'
            disabled={loading || !canSubmit}
            style={{opacity: loading || !canSubmit ? 0.65 : 1}}
          >
            {loading ? 'Guardando...' : 'Crear bloque'}
          </button>
        </form>
      </div>
    </div>
  );
}
