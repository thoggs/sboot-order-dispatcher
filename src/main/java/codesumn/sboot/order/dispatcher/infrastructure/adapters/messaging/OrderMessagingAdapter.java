package codesumn.sboot.order.dispatcher.infrastructure.adapters.messaging;

import codesumn.sboot.order.dispatcher.application.dtos.records.order.OrderRecordDto;
import codesumn.sboot.order.dispatcher.domain.outbound.OrderMessagingPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OrderMessagingAdapter implements OrderMessagingPort {

    private final RabbitTemplate rabbitTemplate;
    private final String responseExchange;
    private final String responseRoutingKey;
    private final ObjectMapper objectMapper;

    public OrderMessagingAdapter(
            RabbitTemplate rabbitTemplate,
            @Value("${spring.rabbitmq.processor.response.exchange}") String responseExchange,
            @Value("${spring.rabbitmq.processor.response.routing.key}") String responseRoutingKey,
            ObjectMapper objectMapper
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.responseExchange = responseExchange;
        this.responseRoutingKey = responseRoutingKey;
        this.objectMapper = objectMapper;
    }

    @Override
    public void sendOrder(OrderRecordDto order) {
        try {
            byte[] messageBytes = objectMapper.writeValueAsBytes(order);
            Message message = new Message(messageBytes);
            rabbitTemplate.send(responseExchange, responseRoutingKey, message);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao serializar mensagem para JSON", e);
        }
    }
}
