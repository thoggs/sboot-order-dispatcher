package codesumn.sboot.order.dispatcher.infrastructure.adapters.messaging;

import codesumn.sboot.order.dispatcher.application.dtos.records.order.OrderRecordDto;
import codesumn.sboot.order.dispatcher.domain.inbound.OrderProcessorPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderResponseMessagingAdapter {

    private final OrderProcessorPort orderProcessorPort;
    private final ObjectMapper objectMapper;

    public OrderResponseMessagingAdapter(OrderProcessorPort orderProcessorPort, ObjectMapper objectMapper) {
        this.orderProcessorPort = orderProcessorPort;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "${spring.rabbitmq.order.queue}")
    public void consumeOrderResponse(byte[] messageBytes) {
        try {
            OrderRecordDto order = objectMapper.readValue(messageBytes, OrderRecordDto.class);

            orderProcessorPort.processOrder(order);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao desserializar mensagem", e);
        }
    }
}