const express = require('express');
const swaggerJsdoc = require('swagger-jsdoc');
const swaggerUi = require('swagger-ui-express');
const app = express();

app.use(express.json());

const swaggerSpec = swaggerJsdoc({
  definition: {
    openapi: '3.0.0',
    info: {
      title: 'Catalogo Service API',
      version: '1.0.0',
      description: 'API para materias y disponibilidad de tutorias'
    }
  },
  apis: ['./src/modules/**/*.routes.js']
});

app.use('/api/docs', swaggerUi.serve, swaggerUi.setup(swaggerSpec));
app.use('/api/materias', require('./modules/materias/interfaces/materia.routes'));
app.use('/api/bloques', require('./modules/disponibilidad/interfaces/disponibilidad.routes'));
app.get('/api/health', (req, res) => res.send('OK'));

module.exports = app;
