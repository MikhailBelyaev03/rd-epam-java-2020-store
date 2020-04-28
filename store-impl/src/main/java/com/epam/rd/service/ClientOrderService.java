package com.epam.rd.service;

import com.epam.rd.entity.ClientOrder;
import java.util.Map;
import java.util.UUID;

public interface ClientOrderService {

    ClientOrder create(Map<UUID, Integer> orderItems);

    ClientOrder findById(UUID clientOrderId);

    void markAsPaid(UUID clientOrderId);
}
