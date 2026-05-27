const router = require('express').Router();
const Repo = require('../infrastructure/disponibilidad.repository');
const Service = require('../application/disponibilidad.service');
const Controller = require('./disponibilidad.controller');
const RabbitMQPublisher = require('../infrastructure/rabbitmq.publisher');
const CrearBloqueCommandHandler = require('../application/commands/CrearBloqueCommandHandler');
const CambiarEstadoCommandHandler = require('../application/commands/CambiarEstadoCommandHandler');
const GetBloqueQueryHandler = require('../application/queries/GetBloqueQueryHandler');
const GetDisponibilidadQueryHandler = require('../application/queries/GetDisponibilidadQueryHandler');

const repo = new Repo();
const eventPublisher = new RabbitMQPublisher();
const service = new Service({
  crearBloqueHandler: new CrearBloqueCommandHandler(repo, eventPublisher),
  cambiarEstadoHandler: new CambiarEstadoCommandHandler(repo, eventPublisher),
  getBloqueHandler: new GetBloqueQueryHandler(repo),
  getDisponibilidadHandler: new GetDisponibilidadQueryHandler(repo)
});
const controller = new Controller(service);

/**
 * @swagger
 * /api/bloques:
 *   post:
 *     summary: Crea un bloque de disponibilidad del tutor
 *     tags: [Disponibilidad]
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required: [tutor_id, materia_id, fecha_inicio, fecha_fin]
 *             properties:
 *               tutor_id:
 *                 type: string
 *               materia_id:
 *                 type: integer
 *               fecha_inicio:
 *                 type: string
 *                 example: "2026-05-27 08:00:00"
 *               fecha_fin:
 *                 type: string
 *                 example: "2026-05-27 10:00:00"
 *     responses:
 *       201:
 *         description: Bloque creado y evento publicado
 */
router.post('/', controller.crear);

// ---- Endpoints requeridos por reservas-service (Nicolas) ----
/**
 * @swagger
 * /api/bloques/{bloqueId}/disponible:
 *   get:
 *     summary: Obtiene el estado y horario de un bloque
 *     tags: [Disponibilidad]
 */
router.get('/:bloqueId/disponible', controller.verificarDisponible);
/**
 * @swagger
 * /api/bloques/{bloqueId}/bloquear:
 *   put:
 *     summary: Cambia el bloque a RESERVADO y publica evento
 *     tags: [Disponibilidad]
 */
router.put('/:bloqueId/bloquear', controller.bloquear);
/**
 * @swagger
 * /api/bloques/{bloqueId}/liberar:
 *   put:
 *     summary: Cambia el bloque a LIBRE y publica evento
 *     tags: [Disponibilidad]
 */
router.put('/:bloqueId/liberar', controller.liberar);

// ---- Endpoint para frontend ----
/**
 * @swagger
 * /api/bloques/materia/{materiaId}:
 *   get:
 *     summary: Lista bloques libres por materia
 *     tags: [Disponibilidad]
 */
router.get('/materia/:materiaId', controller.franjasPorMateria);

/**
 * @swagger
 * /api/bloques/tutor/{tutorId}:
 *   get:
 *     summary: Lista disponibilidad creada por tutor
 *     tags: [Disponibilidad]
 */
router.get('/tutor/:tutorId', controller.getByTutor);
module.exports = router;
