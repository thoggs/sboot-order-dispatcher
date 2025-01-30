package codesumn.sboot.order.dispatcher.shared.exceptions.errors;

public class DuplicateOrderException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Pedido duplicado! Já existe um pedido com os mesmos itens.";

    public DuplicateOrderException() {
        super(DEFAULT_MESSAGE);
    }

    public DuplicateOrderException(String message) {
        super(message);
    }
}
