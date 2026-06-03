const swaggerJsdoc = require('swagger-jsdoc');

const options = {
  definition: {
    openapi: '3.0.0',
    info: {
      title: 'Catálogo Service API',
      version: '1.0.0',
      description: 'API de materias y disponibilidad de tutores',
    },
  },
  apis: ['./src/modules/**/interfaces/*.routes.js'],
};

module.exports = swaggerJsdoc(options);