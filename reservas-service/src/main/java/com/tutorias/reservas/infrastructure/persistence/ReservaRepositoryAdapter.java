package com.tutorias.reservas.infrastructure.persistence;

import com.tutorias.reservas.domain.model.Reserva;
import com.tutorias.reservas.domain.ports.out.ReservaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador de salida: implementa ReservaRepositoryPort usando JPA.
 * Traduce entre Reserva (dominio) y ReservaJpaEntity (infraestructura).
 */
@Component
@RequiredArgsConstructor
public class ReservaRepositoryAdapter implements ReservaRepositoryPort {

    private final ReservaJpaRepository jpaRepository;

    @Override
    public Reserva save(Reserva reserva) {
        ReservaJpaEntity entity = ReservaJpaEntity.fromDomain(reserva);
        ReservaJpaEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Reserva> findById(Long id) {
        return jpaRepository.findById(id).map(ReservaJpaEntity::toDomain);
    }

    @Override
    public Optional<Reserva> findReservaActivaEnBloque(Long bloqueId, LocalDate fechaSesion) {
        return jpaRepository.findReservaActivaEnBloque(bloqueId, fechaSesion)
                .map(ReservaJpaEntity::toDomain);
    }

    @Override
    public boolean existeReservaDuplicada(String estudianteId, String tutorId, LocalDate fechaSesion) {
        return jpaRepository.existeReservaDuplicada(estudianteId, tutorId, fechaSesion);
    }

    @Override
    public List<Reserva> findProximasEstudiante(String estudianteId, LocalDate hoy) {
        return jpaRepository.findProximasEstudiante(estudianteId, hoy)
                .stream().map(ReservaJpaEntity::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Reserva> findProximasTutor(String tutorId, LocalDate hoy) {
        return jpaRepository.findProximasTutor(tutorId, hoy)
                .stream().map(ReservaJpaEntity::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Reserva> findHistorialEstudiante(String estudianteId) {
        return jpaRepository.findHistorialEstudiante(estudianteId)
                .stream().map(ReservaJpaEntity::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Reserva> findPendientesAsistencia(String tutorId, LocalDate hoy) {
        return jpaRepository.findPendientesAsistencia(tutorId, hoy)
                .stream().map(ReservaJpaEntity::toDomain).collect(Collectors.toList());
    }
}
