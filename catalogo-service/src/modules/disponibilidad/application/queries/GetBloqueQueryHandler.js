class GetBloqueQueryHandler {
  constructor(repository) {
    this.repository = repository;
  }

  async execute(query) {
    return this.repository.findById(query.bloqueId);
  }
}

module.exports = GetBloqueQueryHandler;
