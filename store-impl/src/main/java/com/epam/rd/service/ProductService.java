package com.epam.rd.service;

import java.util.Map;
import java.util.UUID;

public interface ProductService {

    void calculateStock();

    void receiveDelivery(Map<UUID, Integer> deliveryItems);

    void reserveProduct(UUID productId, int quantity);
}
