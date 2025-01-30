package codesumn.sboot.order.dispatcher.domain.inbound;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;

public interface OrderResponseMessagingPort {
    @RabbitListener(queues = "${spring.rabbitmq.order.queue}", ackMode = "MANUAL")
    void consumeOrderResponse(Message message, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel);
}
