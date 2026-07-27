package com.gmail.voronovskyi.yaroslav.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "enum Currency", enumAsRef = true)
public enum Currency {

    @Schema(description = "Currency - UAH")
    UAH,

    @Schema(description = "Currency - USD")
    USD,

    @Schema(description = "Currency - EUR")
    EUR
}
