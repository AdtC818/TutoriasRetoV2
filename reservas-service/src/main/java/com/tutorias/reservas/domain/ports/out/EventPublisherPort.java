package com.tutorias.reservas.domain.ports.out;

/**
 * Puerto de salida: abstracción de mensajería.
 * Los handlers publican eventos de dominio a través de este puerto.
 */
public interface EventPublisherPort {
    void publish(Object event);
}
