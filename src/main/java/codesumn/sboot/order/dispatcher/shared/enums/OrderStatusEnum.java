package codesumn.sboot.order.dispatcher.shared.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatusEnum {

    PENDING("pending"),
    PROCESSING("processing"),
    PROCESSED("processed"),
    CANCELED("canceled");

    private final String value;

}
