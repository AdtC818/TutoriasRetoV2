package com.tutorias.reservas.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA. Solo conoce ReservaJpaEntity.
 */
@Repository
public interface ReservaJpaRepository extends JpaRepository<ReservaJpaEntity, Long> {

    @Query("SELECT r FROM ReservaJpaEntity r WHERE r.bloqueDisponibilidadId = :bloqueId " +
           "AND r.fechaSesion = :fechaSesion AND r.estado = 'ACTIVA'")
    Optional<ReservaJpaEntity> findReservaActivaEnBloque(
            @Param("bloqueId") Long bloqueId,
            @Param("fechaSesion") LocalDate fechaSesion);

    @Query("SELECT r FROM ReservaJpaEntity r WHERE r.estudianteId = :estudianteId " +
           "AND r.fechaSesion >= :hoy AND r.estado = 'ACTIVA' ORDER BY r.fechaSesion ASC")
    List<ReservaJpaEntity> findProximasEstudiante(
            @Param("estudianteId") String estudianteId,
            @Param("hoy") LocalDate hoy);

    @Query("SELECT r FROM ReservaJpaEntity r WHERE r.tutorId = :tutorId " +
           "AND r.fechaSesion >= :hoy AND r.estado = 'ACTIVA' ORDER BY r.fechaSesion ASC")
    List<ReservaJpaEntity> findProximasTutor(
            @Param("tutorId") String tutorId,
            @Param("hoy") LocalDate hoy);

    @Query("SELECT r FROM ReservaJpaEntity r WHERE r.estudianteId = :estudianteId " +
           "ORDER BY r.fechaSesion DESC")
    List<ReservaJpaEntity> findHistorialEstudiante(
            @Param("estudianteId") String estudianteId);

    @Query("SELECT r FROM ReservaJpaEntity r WHERE r.tutorId = :tutorId " +
           "AND r.fechaSesion < :hoy AND r.estado = 'ACTIVA'")
    List<ReservaJpaEntity> findPendientesAsistencia(
            @Param("tutorId") String tutorId,
            @Param("hoy") LocalDate hoy);

    @Query("SELECT COUNT(r) > 0 FROM ReservaJpaEntity r WHERE r.estudianteId = :estudianteId " +
           "AND r.tutorId = :tutorId AND r.fechaSesion = :fechaSesion AND r.estado = 'ACTIVA'")
    boolean existeReservaDuplicada(
            @Param("estudianteId") String estudianteId,
            @Param("tutorId") String tutorId,
            @Param("fechaSesion") LocalDate fechaSesion);
}
