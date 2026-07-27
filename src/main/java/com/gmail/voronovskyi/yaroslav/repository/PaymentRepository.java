package com.gmail.voronovskyi.yaroslav.repository;

import com.gmail.voronovskyi.yaroslav.model.Payment;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByExternalId(UUID externalId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
                select p
                from Payment p
                where p.id = :id
            """)
    Optional<Payment> findByIdForUpdate(UUID id);
}
