package com.epam.rd.service.impl;

import com.epam.rd.entity.SupplierOrder;
import com.epam.rd.service.SupplierOrderService;
import java.util.Map;
import java.util.UUID;

public class SupplierOrderServiceImpl implements SupplierOrderService {

    @Override
    public SupplierOrder create(Map<UUID, Integer> orderItems) {
        return null;
    }

    @Override
    public void markAsDelivered(UUID supplierOrderId) {

    }

    @Override
    public SupplierOrder checkOrderByPaymentId(UUID paymentId) {
        return null;
    }
}
