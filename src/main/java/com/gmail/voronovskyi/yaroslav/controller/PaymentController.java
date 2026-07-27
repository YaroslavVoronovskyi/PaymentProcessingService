package com.gmail.voronovskyi.yaroslav.controller;

import com.gmail.voronovskyi.yaroslav.dto.request.CreatePaymentRequest;
import com.gmail.voronovskyi.yaroslav.dto.response.PaymentResponse;
import com.gmail.voronovskyi.yaroslav.exception.ApiError;
import com.gmail.voronovskyi.yaroslav.service.PaymentService;
import com.gmail.voronovskyi.yaroslav.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payments API")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constants.CODE_400, description = Constants.HTTP_STATUS_BAD_REQUEST +
                    ", ErrorKey [ BAD_REQUEST, INVALID_DATA ]",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @Operation(summary = "Create payment", description = "Create payment by CreatePaymentRequest")
    public PaymentResponse create(@Valid @RequestBody CreatePaymentRequest request) {
        log.debug("REST request to create payment, externalId={}", request.externalId());
        PaymentResponse paymentResponse = paymentService.create(request);
        log.debug("Successfully created payment by REST request with externalId={}", request.externalId());
        return paymentResponse;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constants.CODE_404, description = Constants.HTTP_STATUS_NOT_FOUND +
                    ", ErrorKey [ ENTITY_NOT_FOUND ]",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @Operation(summary = "Retrieve payment by id", description = "Retrieve payment by UUID")
    public PaymentResponse get(@PathVariable("id") UUID id) {
        log.debug("Rest request to retrieve payment, id={}", id);
        PaymentResponse response = paymentService.get(id);
        log.debug("Successfully retrieved payment by REST request with id={}", id);
        return response;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Retrieve all payments", description = "Retrieve all payments with Pageable")
    public Page<PaymentResponse> getAll(@ParameterObject Pageable pageable) {
        log.debug("Rest request to retrieve all payments");
        Page<PaymentResponse> responses = paymentService.getAll(pageable);
        log.debug("{} - payments were successfully retrieved by REST request", responses.getTotalElements());
        return responses;
    }

    @GetMapping("/external/{externalId}")
    public PaymentResponse getByExternalId(@PathVariable("externalId") UUID externalId) {
        log.debug("Rest request to retrieve payment by externalId={}", externalId);
        PaymentResponse response = paymentService.getByExternalId(externalId);
        log.debug("Successfully retrieved payment by REST request by externalId={}", externalId);
        return response;
    }
}
