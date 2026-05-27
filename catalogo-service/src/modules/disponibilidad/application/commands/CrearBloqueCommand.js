class CrearBloqueCommand {
  constructor({ tutor_id, materia_id, fecha_inicio, fecha_fin }) {
    this.tutor_id = tutor_id;
    this.materia_id = materia_id;
    this.fecha_inicio = fecha_inicio;
    this.fecha_fin = fecha_fin;
  }
}

module.exports = CrearBloqueCommand;
