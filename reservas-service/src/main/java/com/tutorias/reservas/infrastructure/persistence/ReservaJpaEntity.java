package com.tutorias.reservas.infrastructure.persistence;

import com.tutorias.reservas.domain.model.EstadoReserva;
import com.tutorias.reservas.domain.model.Reserva;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad JPA de persistencia.
 * Vive en infrastructure — el dominio no conoce anotaciones de JPA.
 */
@Entity
@Table(name = "reservas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "estudiante_id", nullable = false)
    private String estudianteId;

    @Column(name = "tutor_id", nullable = false)
    private String tutorId;

    @Column(name = "bloque_disponibilidad_id", nullable = false)
    private Long bloqueDisponibilidadId;

    @Column(name = "materia_id", nullable = false)
    private String materiaId;

    @Column(name = "fecha_sesion", nullable = false)
    private LocalDate fechaSesion;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;

    @Column(name = "fecha_hora_creacion", nullable = false)
    private LocalDateTime fechaHoraCreacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoReserva estado;

    @Column(name = "notas_estudiante", length = 500)
    private String notasEstudiante;

    @PrePersist
    protected void onCreate() {
        if (this.fechaCreacion == null) this.fechaCreacion = LocalDate.now();
        if (this.fechaHoraCreacion == null) this.fechaHoraCreacion = LocalDateTime.now();
        if (this.estado == null) this.estado = EstadoReserva.ACTIVA;
    }

    // ===== Mappers =====

    public static ReservaJpaEntity fromDomain(Reserva r) {
        return ReservaJpaEntity.builder()
                .id(r.getId())
                .estudianteId(r.getEstudianteId())
                .tutorId(r.getTutorId())
                .bloqueDisponibilidadId(r.getBloqueDisponibilidadId())
                .materiaId(r.getMateriaId())
                .fechaSesion(r.getFechaSesion())
                .fechaCreacion(r.getFechaCreacion())
                .fechaHoraCreacion(r.getFechaHoraCreacion())
                .estado(r.getEstado())
                .notasEstudiante(r.getNotasEstudiante())
                .build();
    }

    public Reserva toDomain() {
        return Reserva.reconstituir(
                this.id,
                this.estudianteId,
                this.tutorId,
                this.bloqueDisponibilidadId,
                this.materiaId,
                this.fechaSesion,
                this.fechaCreacion,
                this.fechaHoraCreacion,
                this.estado,
                this.notasEstudiante
        );
    }
}
