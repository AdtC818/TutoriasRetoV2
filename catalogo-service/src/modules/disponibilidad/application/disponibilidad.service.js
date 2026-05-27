const CrearBloqueCommand = require('./commands/CrearBloqueCommand');
const CambiarEstadoCommand = require('./commands/CambiarEstadoCommand');
const GetBloqueQuery = require('./queries/GetBloqueQuery');
const GetDisponibilidadQuery = require('./queries/GetDisponibilidadQuery');

class DisponibilidadService {
  constructor({ crearBloqueHandler, cambiarEstadoHandler, getBloqueHandler, getDisponibilidadHandler }) {
    this.crearBloqueHandler = crearBloqueHandler;
    this.cambiarEstadoHandler = cambiarEstadoHandler;
    this.getBloqueHandler = getBloqueHandler;
    this.getDisponibilidadHandler = getDisponibilidadHandler;
  }

  async crearBloque(data) {
    return this.crearBloqueHandler.execute(new CrearBloqueCommand(data));
  }

  async obtenerBloque(bloqueId) {
    return this.getBloqueHandler.execute(new GetBloqueQuery(bloqueId));
  }

  async cambiarEstado(bloqueId, nuevoEstado) {
    return this.cambiarEstadoHandler.execute(
      new CambiarEstadoCommand({ bloqueId, estado: nuevoEstado })
    );
  }

  async getFranjasPorMateria(materiaId) {
    return this.getDisponibilidadHandler.execute(new GetDisponibilidadQuery({ materiaId }));
  }

  async getDisponibilidadTutor(tutorId) {
    return this.getDisponibilidadHandler.execute(new GetDisponibilidadQuery({ tutorId }));
  }
}

module.exports = DisponibilidadService;
