package com.epam.rd.repository;

import com.epam.rd.entity.SupplierOrderItem;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

/**
 * @author Belousov Anton
 * @{code SupplierOrderItemRepository} describe work with table st_supplier_order_item on DB.
 */

public interface SupplierOrderItemRepository extends CrudRepository<SupplierOrderItem, UUID> {
}
