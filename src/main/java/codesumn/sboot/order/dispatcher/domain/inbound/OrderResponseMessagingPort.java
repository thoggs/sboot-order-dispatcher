package codesumn.sboot.order.dispatcher.domain.inbound;

import codesumn.sboot.order.dispatcher.application.dtos.records.order.OrderRecordDto;

public interface OrderResponseMessagingPort {
    void consumeOrderResponse(OrderRecordDto order);
}
