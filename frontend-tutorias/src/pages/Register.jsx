import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { commandClient } from '../api/commands';
import Swal from 'sweetalert2';
import './Register.css';

export default function Register() {
  const [nombre, setNombre] = useState('');
  const [correo, setCorreo] = useState('');
  const [password, setPassword] = useState('');
  const [confirmarPassword, setConfirmarPassword] = useState('');
  const [rol, setRol] = useState('ESTUDIANTE');
  const navigate = useNavigate();

  const handleRegister = async (e) => {
    e.preventDefault();

    if (password !== confirmarPassword) {
      Swal.fire({
        title: 'Atención',
        text: 'Las contraseñas no coinciden.',
        icon: 'warning'
      });
      return;
    }

    try {
      await commandClient.post('/auth/register', { nombre, correo, password, rol });
      
      Swal.fire({
        title: 'Registro exitoso',
        text: 'Tu cuenta ha sido creada con éxito. Ahora puedes iniciar sesión.',
        icon: 'success'
      }).then(() => {
        navigate('/login');
      });
    } catch (error) {
      console.error(error);
      const msg = error.response?.data?.detail || 'Ocurrió un error al intentar crear tu cuenta.';
      Swal.fire({
        title: 'Error',
        text: msg,
        icon: 'error'
      });
    }
  };

  return (
    <div className="register-container">
      <div className="bg-shape shape1"></div>
      <div className="bg-shape shape2"></div>

      <div className="register-content">
        <div className="register-left">
          <h1 className="register-logo">Tutorías</h1>
          <h2 className="register-subtitle">
            Únete a nuestra plataforma y empieza a <span className="highlight-text">crecer.</span>
          </h2>
          <div className="image-wrapper">
            <img 
              src="/pixar-graduados.jfif" 
              alt="Graduados universitarios" 
              className="register-image" 
              onError={(e) => {
                e.target.src = 'https://images.unsplash.com/photo-1523050854058-8df90110c9f1?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80';
              }}
            />
          </div>
        </div>

        <div className="register-right">
          <form className="register-card" onSubmit={handleRegister}>
            <input 
              type="text" 
              placeholder="Nombre completo" 
              value={nombre}
              onChange={(e) => setNombre(e.target.value)}
              className="register-input"
              required
            />
            <input 
              type="email" 
              placeholder="Correo electrónico" 
              value={correo}
              onChange={(e) => setCorreo(e.target.value)}
              className="register-input"
              required
            />
            <input 
              type="password" 
              placeholder="Contraseña" 
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="register-input"
              required
            />
            <input 
              type="password" 
              placeholder="Confirmar contraseña" 
              value={confirmarPassword}
              onChange={(e) => setConfirmarPassword(e.target.value)}
              className="register-input"
              required
            />
            <select 
              value={rol}
              onChange={(e) => setRol(e.target.value)}
              className="register-input"
              required
            >
              <option value="ESTUDIANTE">Estudiante</option>
              <option value="TUTOR">Tutor</option>
            </select>
            <button type="submit" className="register-btn">
              Crear cuenta
            </button>
            <div className="register-divider"></div>
            <button type="button" className="register-register-btn" onClick={() => navigate('/login')}>
              Volver a iniciar sesión
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}