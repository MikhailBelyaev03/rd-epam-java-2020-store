package com.epam.rd.repository;

import com.epam.rd.entity.Payment;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Payment repository for working with payment table in database
 *
 * @author YurchenkoDD
 */
public interface PaymentRepository extends CrudRepository<Payment, UUID> {
    Optional<Payment> findBySupplierOrderId(UUID uuid);
}


