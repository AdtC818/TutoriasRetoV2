class GetDisponibilidadQueryHandler {
  constructor(repository) {
    this.repository = repository;
  }

  async execute(query) {
    if (query.materiaId) {
      return this.repository.getDisponibilidadByMateria(query.materiaId);
    }

    if (query.tutorId) {
      return this.repository.getDisponibilidadByTutor(query.tutorId);
    }

    throw new Error('Debe indicar materiaId o tutorId');
  }
}

module.exports = GetDisponibilidadQueryHandler;
