package com.gmail.voronovskyi.yaroslav.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "enum ErrorKey", enumAsRef = true)
public enum ErrorKey {

    @Schema(description = "Incorrect request parameters.")
    BAD_REQUEST("BAD_REQUEST"),

    @Schema(description = "Incorrect data for the request.")
    INVALID_DATA("INVALID_DATA"),

    @Schema(description = "Entity not found.")
    ENTITY_NOT_FOUND("ENTITY_NOT_FOUND"),

    @Schema(description = "Duplicate external ID.")
    DUPLICATE_EXTERNAL_ID("DUPLICATE_EXTERNAL_ID"),

    @Schema(description = "Static resource not found.")
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND");

    private final String key;
}
