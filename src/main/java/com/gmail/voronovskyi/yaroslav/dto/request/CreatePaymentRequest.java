package com.gmail.voronovskyi.yaroslav.dto.request;

import com.gmail.voronovskyi.yaroslav.model.Currency;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentRequest(
        @NotNull
        UUID externalId,

        @NotNull
        @Positive
        @DecimalMin(value = "0.01")
        BigDecimal amount,

        @NotNull
        Currency currency
) implements Serializable {

}
