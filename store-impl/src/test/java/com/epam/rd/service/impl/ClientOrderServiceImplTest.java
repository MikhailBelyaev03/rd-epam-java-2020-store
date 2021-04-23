package com.epam.rd.service.impl;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.epam.rd.Application;
import com.epam.rd.entity.Catalog;
import com.epam.rd.entity.ClientOrder;
import com.epam.rd.entity.ClientOrderItem;
import com.epam.rd.entity.Product;
import com.epam.rd.repository.ClientOrderRepository;
import com.epam.rd.repository.ProductRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = Application.class)
public class ClientOrderServiceImplTest {

    private static final String STATUS_IN_PROGRESS = "IN PROGRESS";

    @Mock
    private ClientOrderRepository clientOrderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    @Autowired
    private ProductServiceImpl productService;

    @InjectMocks
    private ClientOrderServiceImpl clientOrderService;

    @Test
    public void createWhenProductNotFoundedThenThrowException() {
        Map<UUID, Integer> actualOrderItems = new HashMap<>();
        UUID UUIDOrder = UUID.randomUUID();
        actualOrderItems.put(UUIDOrder, 10);
        ClientOrder clientOrder = new ClientOrder();
        clientOrder.setStatus(STATUS_IN_PROGRESS);

        List<ClientOrderItem> clientOrderItemList = new ArrayList<>();
        clientOrder.setPrice(10);
        clientOrder.setClientOrderItems(clientOrderItemList);

        Product expectedProduct = new Product();
        UUID UUIProduct = UUID.randomUUID();
        expectedProduct.setId(UUIProduct);

        when(clientOrderRepository.findById(UUIDOrder)).thenReturn(ofNullable(clientOrder));

        try {
            clientOrderService.create(actualOrderItems);
        } catch (RuntimeException re) {
            String message = "Product not found";
            assertEquals(message, re.getMessage());
        }
    }

    @Test
    public void createWhenProductListNotEmptyThenCreate() {

        ClientOrder expectedClientOrder = new ClientOrder();
        UUID uuidProduct = UUID.randomUUID();
        Product product = new Product();
        product.setId(uuidProduct);
        Catalog actualCatalog = new Catalog();
        product.setCatalog(actualCatalog);
        product.getCatalog().setQuantity(10);

        ClientOrderItem clientOrderItem = new ClientOrderItem();
        clientOrderItem.setClientOrder(expectedClientOrder);
        expectedClientOrder.getClientOrderItems().add(clientOrderItem);
        expectedClientOrder.setPrice(100);
        expectedClientOrder.setStatus("IN PROGRESS");
        UUID UUIDOrderItem = UUID.randomUUID();
        expectedClientOrder.setId(UUIDOrderItem);

        Map<UUID, Integer> orderItem = new HashMap<>();
        orderItem.put(uuidProduct, 15);

        when(productRepository.findById(uuidProduct)).thenReturn(ofNullable(product));

        ClientOrder actualClientOrder = clientOrderService.create(orderItem);
        verify(productService).reserveProduct(uuidProduct, 15);
        verify(clientOrderRepository).save(actualClientOrder);
        verify(productRepository).findById(uuidProduct);
        assertNotNull(actualClientOrder);

    }

    @Test
    public void findByIdWhenUUIDIsNotNullThenReturnOrder() {

        UUID actualUUID = UUID.randomUUID();
        ClientOrder expectedClientOrder = new ClientOrder();
        expectedClientOrder.setId(actualUUID);
        when(clientOrderRepository.findById(actualUUID))
                .thenReturn(of(of(expectedClientOrder).get()));
        ClientOrder actualClientOrder = clientOrderRepository.findById(actualUUID).get();
        assertSame(expectedClientOrder, actualClientOrder);
    }


    @Test
    public void findByIdWhenUUIDIsNullThenReturnNull() {
        UUID actualUUID = UUID.randomUUID();
        when(clientOrderRepository.findById(actualUUID))
                .thenReturn(null);
        Optional<ClientOrder> actualOrder = clientOrderRepository.findById(actualUUID);
        assertNull(actualOrder);
    }

    @Test
    public void markAsPaidWhenClientOrderIsNotNullChangeOrderStatus() {
        UUID actualUUID = UUID.fromString("556d8eee-7e64-4c10-929a-56bdc0de4aa3");
        ClientOrder expectedClientOrder = new ClientOrder();
        expectedClientOrder.setId(actualUUID);
        expectedClientOrder.setStatus(STATUS_IN_PROGRESS);
        expectedClientOrder.setPrice(10);
        expectedClientOrder.setPaymentId(UUID.randomUUID());

        doReturn(of(expectedClientOrder)).when(clientOrderRepository).findById(actualUUID);

        clientOrderService.markAsPaid(actualUUID);

        verify(clientOrderRepository).findById(actualUUID);
        ClientOrder actualClientOrder = clientOrderRepository.findById(actualUUID).get();
        verify(clientOrderRepository).save(actualClientOrder);

        assertEquals(expectedClientOrder.getStatus(), "PAID");
    }


    @Test
    public void markAsPaidWhenClientOrderIsNullThenThrowException() {
        UUID actualUUID = UUID.randomUUID();
        try {
            clientOrderRepository.findById(actualUUID);
        } catch (RuntimeException re) {
            String message = "ClientOrder not found";
            assertEquals(message, re.getMessage());
            throw re;
        }

    }
}