const dayjs = require('dayjs');
const Bloque = require('../../domain/bloque.entity');

class CrearBloqueCommandHandler {
  constructor(repository, eventPublisher) {
    this.repository = repository;
    this.eventPublisher = eventPublisher;
  }

  async execute(command) {
    const bloque = new Bloque(command);
    const inicio = dayjs(bloque.fecha_inicio);
    const fin = dayjs(bloque.fecha_fin);

    const conflicto = await this.repository.existeSolapamiento(
      bloque.tutor_id,
      bloque.fecha_inicio,
      bloque.fecha_fin
    );
    if (conflicto) throw new Error('Conflicto de horario');

    const bloqueId = await this.repository.crearBloque(bloque);

    let actual = inicio;
    while (actual.isBefore(fin)) {
      const siguiente = actual.add(30, 'minute');
      await this.repository.crearFranja(
        bloqueId,
        actual.format('YYYY-MM-DD HH:mm:ss'),
        siguiente.format('YYYY-MM-DD HH:mm:ss')
      );
      actual = siguiente;
    }

    await this.eventPublisher.publish('catalogo.bloque.creado', {
      type: 'BloqueCreado',
      bloqueId,
      tutorId: bloque.tutor_id,
      materiaId: bloque.materia_id,
      fechaInicio: bloque.fecha_inicio,
      fechaFin: bloque.fecha_fin,
      estado: bloque.estado,
      occurredAt: new Date().toISOString()
    });

    return bloqueId;
  }
}

module.exports = CrearBloqueCommandHandler;
