package com.epam.rd.repository;

import com.epam.rd.entity.SupplierOrder;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Belousov Anton
 * @{code SupplierOrderRepository} describe work with table st_supplier_order on DB.
 */

public interface SupplierOrderRepository extends CrudRepository<SupplierOrder, UUID> {
    Optional<SupplierOrder> findByPaymentId(UUID paymentId);
}
