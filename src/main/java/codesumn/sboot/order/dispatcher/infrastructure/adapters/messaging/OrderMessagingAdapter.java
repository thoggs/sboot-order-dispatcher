package codesumn.sboot.order.dispatcher.infrastructure.adapters.messaging;

import codesumn.sboot.order.dispatcher.application.dtos.records.order.OrderRecordDto;
import codesumn.sboot.order.dispatcher.domain.outbound.OrderMessagingPort;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OrderMessagingAdapter implements OrderMessagingPort {

    private final RabbitTemplate rabbitTemplate;
    private final String responseExchange;
    private final String responseRoutingKey;

    public OrderMessagingAdapter(
            RabbitTemplate rabbitTemplate,
            @Value("${spring.rabbitmq.processor.response.exchange}") String responseExchange,
            @Value("${spring.rabbitmq.processor.response.routing.key}") String responseRoutingKey
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.responseExchange = responseExchange;
        this.responseRoutingKey = responseRoutingKey;
    }

    @Override
    public void sendOrder(OrderRecordDto order) {
        rabbitTemplate.convertAndSend(responseExchange, responseRoutingKey, order);
    }
}
