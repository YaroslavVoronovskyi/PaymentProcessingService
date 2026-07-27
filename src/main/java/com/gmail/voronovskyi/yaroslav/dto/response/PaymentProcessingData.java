package com.gmail.voronovskyi.yaroslav.dto.response;

import com.gmail.voronovskyi.yaroslav.model.Currency;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public record PaymentProcessingData(
        UUID paymentId,
        BigDecimal amount,
        Currency currency
) implements Serializable {

}
