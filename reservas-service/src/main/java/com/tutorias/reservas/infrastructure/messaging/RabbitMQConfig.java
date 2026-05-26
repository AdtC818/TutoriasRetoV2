package com.tutorias.reservas.infrastructure.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ: exchange, colas y bindings para eventos de reservas.
 */
@Configuration
public class RabbitMQConfig {

    // Exchange central para todos los eventos de la plataforma
    public static final String EXCHANGE = "tutorias.events";

    // Routing keys
    public static final String RK_RESERVA_CREADA   = "reserva.creada";
    public static final String RK_RESERVA_CANCELADA = "reserva.cancelada";

    // Nombres de colas
    public static final String QUEUE_RESERVA_CREADA   = "reservas.reserva.creada";
    public static final String QUEUE_RESERVA_CANCELADA = "reservas.reserva.cancelada";

    @Bean
    public TopicExchange tutoriasExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue queueReservaCreada() {
        return new Queue(QUEUE_RESERVA_CREADA, true);
    }

    @Bean
    public Queue queueReservaCancelada() {
        return new Queue(QUEUE_RESERVA_CANCELADA, true);
    }

    @Bean
    public Binding bindingReservaCreada(Queue queueReservaCreada, TopicExchange tutoriasExchange) {
        return BindingBuilder.bind(queueReservaCreada)
                .to(tutoriasExchange)
                .with(RK_RESERVA_CREADA);
    }

    @Bean
    public Binding bindingReservaCancelada(Queue queueReservaCancelada, TopicExchange tutoriasExchange) {
        return BindingBuilder.bind(queueReservaCancelada)
                .to(tutoriasExchange)
                .with(RK_RESERVA_CANCELADA);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                          MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
}
