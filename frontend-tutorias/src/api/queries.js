import axios from 'axios';

// QUERIES CLIENT (CQRS)
// Cliente exclusivo para operaciones que LEEN datos (GET)
export const queryClient = axios.create({
  headers: {
    'Content-Type': 'application/json',
  },
});

queryClient.interceptors.request.use(
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
