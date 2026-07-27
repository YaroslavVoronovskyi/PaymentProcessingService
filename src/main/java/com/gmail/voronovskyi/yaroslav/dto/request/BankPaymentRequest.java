package com.gmail.voronovskyi.yaroslav.dto.request;

import com.gmail.voronovskyi.yaroslav.model.Currency;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public record BankPaymentRequest(
        UUID paymentId,
        BigDecimal amount,
        Currency currency
) implements Serializable {

}
