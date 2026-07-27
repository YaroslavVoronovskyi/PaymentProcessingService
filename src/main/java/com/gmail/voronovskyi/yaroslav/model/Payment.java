package com.gmail.voronovskyi.yaroslav.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@AllArgsConstructor
@Accessors(chain = true)
@RequiredArgsConstructor
@Schema(description = "Payment Entity for documentation")
@Table(name = "payments",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_payment_external_id", columnNames = "external_id")})
public class Payment {

    @Id
    @UuidGenerator
    @GeneratedValue
    private UUID id;

    @Column(name = "external_id", nullable = false, unique = true, updatable = false)
    private UUID externalId;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Column(name = "bank_transaction_id")
    private String bankTransactionId;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Column(name = "created", nullable = false, updatable = false)
    private Instant created;

    @Column(name = "updated", nullable = false)
    private Instant updated;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        created = now;
        updated = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updated = Instant.now();
    }

    public static Payment create(UUID externalId, BigDecimal amount, Currency currency) {
        return new Payment()
                .setExternalId(externalId)
                .setAmount(amount)
                .setCurrency(currency)
                .setStatus(PaymentStatus.NEW);
    }

    public void markProcessing() {
        this.status = PaymentStatus.PROCESSING;
    }

    public void markSuccess(String bankTransactionId) {
        this.status = PaymentStatus.SUCCESS;
        this.bankTransactionId = bankTransactionId;
        this.failureReason = null;
    }

    public void markFailed(String failureReason) {
        this.status = PaymentStatus.FAILED;
        this.failureReason = failureReason;
    }
}
