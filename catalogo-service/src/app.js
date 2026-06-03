const express = require('express');
const swaggerJsdoc = require('swagger-jsdoc');
const swaggerUi = require('swagger-ui-express');
const app = express();
const swaggerSpec = require('./config/swagger');


app.use(express.json());

app.use('/api/docs', swaggerUi.serve, swaggerUi.setup(swaggerSpec));
app.use('/api/materias', require('./modules/materias/interfaces/materia.routes'));
app.use('/api/bloques', require('./modules/disponibilidad/interfaces/disponibilidad.routes'));
app.get('/api/health', (req, res) => res.send('OK'));

module.exports = app;
