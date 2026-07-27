package com.gmail.voronovskyi.yaroslav.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "enum PaymentStatus", enumAsRef = true)
public enum PaymentStatus {

    @Schema(description = "PaymentStatus - NEW")
    NEW,

    @Schema(description = "PaymentStatus - PROCESSING")
    PROCESSING,

    @Schema(description = "PaymentStatus - SUCCESS")
    SUCCESS,

    @Schema(description = "PaymentStatus - FAILED")
    FAILED
}
