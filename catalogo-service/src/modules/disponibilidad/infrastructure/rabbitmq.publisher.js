const amqp = require('amqplib');

const EXCHANGE = process.env.RABBITMQ_EXCHANGE || 'tutorias.events';

class RabbitMQPublisher {
  constructor() {
    this.connection = null;
    this.channel = null;
  }

  async connect() {
    if (this.channel) return this.channel;

    const url = process.env.RABBITMQ_URL || 'amqp://admin:admin@rabbitmq-tutorias:5672';
    this.connection = await amqp.connect(url);
    this.channel = await this.connection.createChannel();
    await this.channel.assertExchange(EXCHANGE, 'topic', { durable: true });
    return this.channel;
  }

  async publish(routingKey, payload) {
    try {
      const channel = await this.connect();
      channel.publish(
        EXCHANGE,
        routingKey,
        Buffer.from(JSON.stringify(payload)),
        {
          contentType: 'application/json',
          persistent: true
        }
      );
      console.log(`Evento publicado a RabbitMQ: ${routingKey}`);
    } catch (error) {
      console.warn(`No se pudo publicar evento RabbitMQ (${routingKey}): ${error.message}`);
    }
  }
}

module.exports = RabbitMQPublisher;
