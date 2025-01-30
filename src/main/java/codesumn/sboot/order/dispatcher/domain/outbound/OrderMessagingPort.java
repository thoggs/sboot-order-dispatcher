package codesumn.sboot.order.dispatcher.domain.outbound;


import codesumn.sboot.order.dispatcher.application.dtos.records.order.OrderRecordDto;

public interface OrderMessagingPort {
    void sendOrder(OrderRecordDto order);
}
