import axios from 'axios';

// COMMANDS CLIENT (CQRS)
// Cliente exclusivo para operaciones que MODIFICAN datos (POST, PUT, PATCH, DELETE)
export const commandClient = axios.create({
  headers: {
    'Content-Type': 'application/json',
  },
});

commandClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);
