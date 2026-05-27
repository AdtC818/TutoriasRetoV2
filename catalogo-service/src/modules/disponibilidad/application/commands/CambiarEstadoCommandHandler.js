class CambiarEstadoCommandHandler {
  constructor(repository, eventPublisher) {
    this.repository = repository;
    this.eventPublisher = eventPublisher;
  }

  async execute(command) {
    if (!['LIBRE', 'RESERVADO', 'DISPONIBLE'].includes(command.estado)) {
      throw new Error('Estado de bloque invalido');
    }

    const estado = command.estado === 'DISPONIBLE' ? 'LIBRE' : command.estado;
    const bloque = await this.repository.findById(command.bloqueId);
    if (!bloque) throw new Error('Bloque no encontrado');

    await this.repository.actualizarEstado(command.bloqueId, estado);

    await this.eventPublisher.publish('catalogo.bloque.estado-cambiado', {
      type: 'BloqueEstadoCambiado',
      bloqueId: Number(command.bloqueId),
      estadoAnterior: bloque.estado,
      estadoNuevo: estado,
      occurredAt: new Date().toISOString()
    });
  }
}

module.exports = CambiarEstadoCommandHandler;
