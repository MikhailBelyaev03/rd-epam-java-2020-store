package com.epam.rd.service.impl;

import com.epam.rd.entity.ClientOrder;
import com.epam.rd.entity.ClientOrderItem;
import com.epam.rd.entity.Product;
import com.epam.rd.repository.ClientOrderRepository;
import com.epam.rd.repository.ProductRepository;
import com.epam.rd.service.ClientOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClientOrderServiceImpl implements ClientOrderService {

    private static final Logger log = LoggerFactory.getLogger(ClientOrderRepository.class);
    private static final String STATUS_IN_PROGRESS = "IN PROGRESS";
    private static final String STATUS_PAID = "PAID";
    @Autowired
    private ClientOrderRepository clientOrderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductServiceImpl productService;

    @Override
    public ClientOrder create(Map<UUID, Integer> orderItems) {
        if (isEmpty(orderItems)) {
            log.warn("OrderItems is null or empty");
            throw new RuntimeException("OrderItems is null or empty");
        }

        ClientOrder clientOrder = new ClientOrder();
        clientOrder.setStatus(STATUS_IN_PROGRESS);
        List<ClientOrderItem> clientOrderItemList = orderItems.keySet()
                .stream()
                .map(this::findProductById)
                .map(product -> createOrderItem(product, orderItems.get(product.getId())))
                .map(clientOrderItem -> enrichClientOrderItemWithClientOrder(clientOrderItem, clientOrder))
                .map(clientOrderItem -> reserveProduct(clientOrderItem, orderItems))
                .collect(Collectors.toList());


        if (clientOrderItemList.isEmpty()) {
            log.info("No one product was found, client order wasn't saved");
            throw new RuntimeException("No one product was found, client order wasn't saved");
        }

        clientOrder.setPrice(calculatePrice(clientOrderItemList));
        clientOrder.setClientOrderItems(clientOrderItemList);
        clientOrderRepository.save(clientOrder);
        log.info("Add to database ClientOrder = {}", clientOrder);
        return clientOrder;

    }

    @Override
    public ClientOrder findById(UUID clientOrderId) {
        return clientOrderRepository.findById(clientOrderId).orElse(null);
    }

    @Override
    public void markAsPaid(UUID clientOrderId) {
        ClientOrder clientOrder = clientOrderRepository.findById(clientOrderId)
                .orElseThrow(() -> createRunTimeExceptionWhenClientOrderNotFound(clientOrderId));
        clientOrder.setStatus(STATUS_PAID);
        clientOrderRepository.save(clientOrder);
        log.info("Changed the status to PAID for ClientOrder with id = {}", clientOrderId);
    }

    private ClientOrderItem createOrderItem(Product product, int quantity) {
        ClientOrderItem clientOrderItem = new ClientOrderItem();
        clientOrderItem.setProduct(product);
        clientOrderItem.setQuantity(quantity);
        return clientOrderItem;
    }

    private <K, V> boolean isEmpty(Map<K, V> map) {
        return map == null || map.isEmpty();
    }

    private RuntimeException createRunTimeExceptionWhenProductNotFound(UUID productId) {
        log.warn("Product with id = {} not found", productId);
        return new RuntimeException("Product not found");
    }

    private RuntimeException createRunTimeExceptionWhenClientOrderNotFound(UUID clientOrderId) {
        log.warn("ClientOrder not found with id = {}", clientOrderId);
        return new RuntimeException("ClientOrder not found");
    }

    private ClientOrderItem reserveProduct(ClientOrderItem clientOrderItem, Map<UUID, Integer> orderItems) {
        UUID uuid = clientOrderItem.getProduct().getId();
        productService.reserveProduct(uuid, orderItems.get(uuid));
        return clientOrderItem;
    }

    private Product findProductById(UUID uuid) {
        return productRepository.findById(uuid).orElseThrow(() -> createRunTimeExceptionWhenProductNotFound(uuid));
    }

    private ClientOrderItem enrichClientOrderItemWithClientOrder(ClientOrderItem clientOrderItem, ClientOrder clientOrder) {
        clientOrderItem.setClientOrder(clientOrder);
        return clientOrderItem;
    }

    private double calculatePrice(List<ClientOrderItem> clientOrderItemList) {
        return clientOrderItemList
                .stream()
                .mapToDouble(item -> item.getQuantity() * item.getProduct().getCatalog().getPrice())
                .sum();
    }

}
