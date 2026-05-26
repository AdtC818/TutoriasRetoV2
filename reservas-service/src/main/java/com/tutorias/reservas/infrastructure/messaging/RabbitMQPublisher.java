package com.tutorias.reservas.infrastructure.messaging;

import com.tutorias.reservas.domain.events.ReservaCanceladaEvent;
import com.tutorias.reservas.domain.events.ReservaCreadaEvent;
import com.tutorias.reservas.domain.ports.out.EventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQPublisher implements EventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(Object event) {
        String routingKey = resolveRoutingKey(event);
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, routingKey, event);
            log.info("Evento publicado a RabbitMQ -> exchange={}, routingKey={}, payload={}",
                    RabbitMQConfig.EXCHANGE, routingKey, event);
        } catch (Exception e) {
            log.error("Error publicando evento a RabbitMQ [routingKey={}]: {}",
                    routingKey, e.getMessage());
        }
    }

    private String resolveRoutingKey(Object event) {
        if (event instanceof ReservaCreadaEvent)    return RabbitMQConfig.RK_RESERVA_CREADA;
        if (event instanceof ReservaCanceladaEvent) return RabbitMQConfig.RK_RESERVA_CANCELADA;
        return "reserva.unknown";
    }
}