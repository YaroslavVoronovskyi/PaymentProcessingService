package com.gmail.voronovskyi.yaroslav.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gmail.voronovskyi.yaroslav.util.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.time.Instant;

@Slf4j
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Schema(description = "ApiError Entity for documentation")
public class ApiError implements Serializable {

    @Schema(example = "Http error status")
    private String status = Constants.ERROR_STATUS;
    @Schema(example = "Http error code")
    private int code;
    @Schema(example = "Http error message")
    private String message;
    @Schema(example = "Http error details")
    private String details;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(implementation = ErrorKey.class, example = "Possible ErrorKey for this case")
    private ErrorKey key;
    @Schema(example = "0")
    private Instant created;

    public ApiError(HttpStatus status, Exception exception, String message, ErrorKey key) {
        this.code = status.value();
        this.message = message != null ? message : status.getReasonPhrase();
        this.details = exception.getMessage();
        this.key = key;
        this.created = Instant.now();
    }
}
