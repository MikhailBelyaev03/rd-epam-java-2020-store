package com.epam.rd.service;

import com.epam.rd.entity.SupplierOrder;
import java.util.Map;
import java.util.UUID;

public interface SupplierOrderService {

    SupplierOrder create(Map<UUID, Integer> orderItems);

    void markAsDelivered(UUID supplierOrderId);

    SupplierOrder checkOrderByPaymentId(UUID paymentId);
}
