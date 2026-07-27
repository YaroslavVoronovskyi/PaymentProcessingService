package com.gmail.voronovskyi.yaroslav.dto.response;

import com.gmail.voronovskyi.yaroslav.model.BankPaymentStatus;

import java.io.Serializable;

public record BankPaymentResponse(
        BankPaymentStatus status,
        String transactionId,
        String failureReason
) implements Serializable {

}
