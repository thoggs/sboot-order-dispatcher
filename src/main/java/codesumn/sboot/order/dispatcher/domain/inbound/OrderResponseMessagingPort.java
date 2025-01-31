package codesumn.sboot.order.dispatcher.domain.inbound;

public interface OrderResponseMessagingPort {
    void consumeOrderResponse(byte[] message);
}
