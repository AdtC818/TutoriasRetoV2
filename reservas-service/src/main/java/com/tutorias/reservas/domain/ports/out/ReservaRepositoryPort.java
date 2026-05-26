package com.tutorias.reservas.domain.ports.out;

import com.tutorias.reservas.domain.model.Reserva;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida: abstracción de persistencia.
 * El dominio conoce esta interfaz; la infraestructura la implementa.
 */
public interface ReservaRepositoryPort {

    Reserva save(Reserva reserva);

    Optional<Reserva> findById(Long id);

    Optional<Reserva> findReservaActivaEnBloque(Long bloqueId, LocalDate fechaSesion);

    boolean existeReservaDuplicada(String estudianteId, String tutorId, LocalDate fechaSesion);

    List<Reserva> findProximasEstudiante(String estudianteId, LocalDate hoy);

    List<Reserva> findProximasTutor(String tutorId, LocalDate hoy);

    List<Reserva> findHistorialEstudiante(String estudianteId);

    List<Reserva> findPendientesAsistencia(String tutorId, LocalDate hoy);
}
