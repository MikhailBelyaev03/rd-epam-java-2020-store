package com.epam.rd.service.impl;

import com.epam.rd.entity.ClientOrder;
import com.epam.rd.service.ClientOrderService;
import java.util.Map;
import java.util.UUID;

public class ClientOrderServiceImpl implements ClientOrderService {

    @Override
    public ClientOrder create(Map<UUID, Integer> orderItems) {
        return null;
    }

    @Override
    public ClientOrder findById(UUID clientOrderId) {
        return null;
    }

    @Override
    public void markAsPaid(UUID clientOrderId) {

    }
}
