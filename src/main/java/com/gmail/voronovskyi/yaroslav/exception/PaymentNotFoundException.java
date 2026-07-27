package com.gmail.voronovskyi.yaroslav.exception;

import java.util.UUID;

public class PaymentNotFoundException extends RuntimeException {

    public PaymentNotFoundException(UUID id) {
        super(String.format("Payment with ID '%s' not found.", id));
    }
}
