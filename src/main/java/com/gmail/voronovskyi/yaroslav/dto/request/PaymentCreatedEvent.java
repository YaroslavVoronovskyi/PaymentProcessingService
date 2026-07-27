package com.gmail.voronovskyi.yaroslav.dto.request;

import java.io.Serializable;
import java.util.UUID;

public record PaymentCreatedEvent(
        UUID paymentId
) implements Serializable {

}
