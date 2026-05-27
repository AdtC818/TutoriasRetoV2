class CambiarEstadoCommand {
  constructor({ bloqueId, estado }) {
    this.bloqueId = bloqueId;
    this.estado = estado;
  }
}

module.exports = CambiarEstadoCommand;
