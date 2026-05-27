class BloqueDisponibilidad {
  constructor({ tutor_id, materia_id, fecha_inicio, fecha_fin, estado = 'LIBRE' }) {
    if (!tutor_id) {
      throw new Error('El tutor_id es obligatorio');
    }

    if (!materia_id) {
      throw new Error('La materia_id es obligatoria');
    }

    if (!fecha_inicio || !fecha_fin) {
      throw new Error('Las fechas son obligatorias');
    }

    const inicio = new Date(fecha_inicio);
    const fin = new Date(fecha_fin);

    if (isNaN(inicio) || isNaN(fin)) {
      throw new Error('Formato de fecha invalido');
    }

    if (fin <= inicio) {
      throw new Error('La fecha_fin debe ser mayor a fecha_inicio');
    }

    if (!['LIBRE', 'RESERVADO', 'DISPONIBLE'].includes(estado)) {
      throw new Error('Estado de bloque invalido');
    }

    this.tutor_id = tutor_id;
    this.materia_id = materia_id;
    this.fecha_inicio = inicio;
    this.fecha_fin = fin;
    this.estado = estado === 'DISPONIBLE' ? 'LIBRE' : estado;
  }

  duracionEnMinutos() {
    return (this.fecha_fin - this.fecha_inicio) / 60000;
  }

  esValido() {
    return this.duracionEnMinutos() > 0;
  }
}

module.exports = BloqueDisponibilidad;
