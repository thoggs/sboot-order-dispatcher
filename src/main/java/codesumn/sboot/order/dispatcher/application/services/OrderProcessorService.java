package codesumn.sboot.order.dispatcher.application.services;

import codesumn.sboot.order.dispatcher.application.dtos.records.order.OrderItemRecordDto;
import codesumn.sboot.order.dispatcher.application.dtos.records.order.OrderRecordDto;
import codesumn.sboot.order.dispatcher.domain.inbound.OrderProcessorPort;
import codesumn.sboot.order.dispatcher.domain.outbound.OrderMessagingPort;
import codesumn.sboot.order.dispatcher.shared.enums.OrderStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderProcessorService implements OrderProcessorPort {

    private final OrderMessagingPort orderMessagingPort;

    @Autowired
    public OrderProcessorService(OrderMessagingPort messagingPort) {
        this.orderMessagingPort = messagingPort;
    }

    @Override
    public void processOrder(OrderRecordDto order) {
        BigDecimal totalPrice = calculateTotalPrice(order.items());

        OrderRecordDto orderProcessed = new OrderRecordDto(
                order.id(),
                order.customerName(),
                OrderStatusEnum.PROCESSED,
                totalPrice,
                order.items()
        );

        orderMessagingPort.sendOrder(orderProcessed);
    }

    private BigDecimal calculateTotalPrice(List<OrderItemRecordDto> items) {
        return items.stream()
                .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}