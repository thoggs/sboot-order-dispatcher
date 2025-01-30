package codesumn.sboot.order.dispatcher.infrastructure.adapters.messaging;

import codesumn.sboot.order.dispatcher.application.dtos.records.order.OrderRecordDto;
import codesumn.sboot.order.dispatcher.domain.inbound.OrderProcessorPort;
import codesumn.sboot.order.dispatcher.domain.inbound.OrderResponseMessagingPort;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderResponseMessagingAdapter implements OrderResponseMessagingPort {

    private final OrderProcessorPort orderProcessorPort;

    @Autowired
    public OrderResponseMessagingAdapter(OrderProcessorPort orderProcessorPort) {
        this.orderProcessorPort = orderProcessorPort;
    }

    @RabbitListener(queues = "${spring.rabbitmq.order.queue}")
    public void consumeOrderResponse(OrderRecordDto order) {
        orderProcessorPort.processOrder(order);
    }
}