package codesumn.sboot.order.dispatcher.infrastructure.adapters.messaging;

import codesumn.sboot.order.dispatcher.application.dtos.records.order.OrderRecordDto;
import codesumn.sboot.order.dispatcher.domain.inbound.OrderProcessorPort;
import codesumn.sboot.order.dispatcher.domain.inbound.OrderResponseMessagingPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.amqp.support.AmqpHeaders;
import com.rabbitmq.client.Channel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

@Component
public class OrderResponseMessagingAdapter implements OrderResponseMessagingPort {

    private final OrderProcessorPort orderProcessorPort;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public OrderResponseMessagingAdapter(OrderProcessorPort orderProcessorPort) {
        this.orderProcessorPort = orderProcessorPort;
    }

    @RabbitListener(queues = "${spring.rabbitmq.order.queue}", ackMode = "MANUAL")
    @Override
    public void consumeOrderResponse(
            Message message,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
            Channel channel
    ) {
        try {
            byte[] decompressedBody = decompress(message.getBody());

            OrderRecordDto order = objectMapper.readValue(decompressedBody, OrderRecordDto.class);

            orderProcessorPort.processOrder(order);

            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            System.err.println("Erro ao processar pedido: " + e.getMessage());

            try {
                channel.basicNack(deliveryTag, false, true);
            } catch (IOException ioException) {
                System.err.println("Erro ao reencaminhar mensagem: " + ioException.getMessage());
            }
        }
    }

    private byte[] decompress(byte[] compressedData) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(compressedData);
             GZIPInputStream gzipInputStream = new GZIPInputStream(bis)) {
            return gzipInputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao descompactar mensagem", e);
        }
    }
}