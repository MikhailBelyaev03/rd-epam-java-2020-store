package com.epam.rd.service.impl;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.doNothing;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.when;
import static org.mockito.Matchers.any;
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
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public class ClientOrderServiceImplTest {

    @Mock
    private ClientOrderRepository clientOrderRepository;

    @Mock
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ClientOrderServiceImpl clientOrderService;

    @Test
    public void testCreateWhenProductExist() {
        ClientOrder expectedClientOrder = new ClientOrder();
        UUID uuidProduct = UUID.fromString("e1712476-8127-11ea-a5f1-001e101f0000");
        Product product = createProduct(uuidProduct, createCatalog(10));
        int quantity = 10;
        ClientOrderItem clientOrderItem = createClientOrderItem(product, quantity);
        expectedClientOrder.getClientOrderItems().add(clientOrderItem);
        expectedClientOrder.setPrice(100);
        expectedClientOrder.setStatus("IN PROGRESS");

        Map<UUID, Integer> orderItem = new HashMap<>();
        orderItem.put(uuidProduct, quantity);

        doNothing().when(productService).reserveProduct(uuidProduct, quantity);
        doNothing().when(clientOrderRepository).save(any(ClientOrder.class));
        when(productRepository.findById(uuidProduct)).thenReturn(ofNullable(product));

        ClientOrder actualClientOrder = clientOrderService.create(orderItem);

        verify(productService).reserveProduct(uuidProduct, quantity);
        verify(clientOrderRepository).save(any(ClientOrder.class));
        verify(productRepository).findById(uuidProduct);

        assertEquals(expectedClientOrder, actualClientOrder);
    }

    @Test
    public void testCreateWhenProductNotExist() {
        UUID uuidProduct = UUID.fromString("e1712476-8127-11ea-a5f1-001e101f0000");
        Map<UUID, Integer> orderItem = new HashMap<>();
        orderItem.put(uuidProduct, 10);

        doNothing().when(clientOrderRepository).save(any(ClientOrder.class));
        when(productRepository.findById(uuidProduct)).thenReturn(ofNullable(null));
        ClientOrder actualClientOrder = null;
        try {
            actualClientOrder = clientOrderService.create(orderItem);
            fail("No RuntimeException");
        } catch (RuntimeException e) {

        }

        verify(clientOrderRepository, never()).save(any(ClientOrder.class));
        verify(productRepository).findById(uuidProduct);

        assertNull(actualClientOrder);
    }

    @Test
    public void testCreateWhenMapIsNull() {
        Map<UUID, Integer> orderItem = null;
        ClientOrder actualClientOrder = null;
        try {
            actualClientOrder = clientOrderService.create(orderItem);
            fail("No RuntimeException");
        } catch (RuntimeException e) {

        }
        assertNull(actualClientOrder);
    }

    @Test
    public void testCreateWhenMapIsEmpty() {
        Map<UUID, Integer> orderItem = new HashMap<>();
        ClientOrder actualClientOrder = null;
        try {
            actualClientOrder = clientOrderService.create(orderItem);
            fail("No RuntimeException");
        } catch (RuntimeException e) {

        }
        assertNull(actualClientOrder);
    }

    @Test
    public void testFindByIdWhenExist() {
        UUID uuid = UUID.fromString("5ad8e8a5-5973-4c75-b0f3-c3c29428fa1a");
        ClientOrder clientOrder = new ClientOrder();
        clientOrder.setId(uuid);

        when(clientOrderRepository.findById(uuid)).thenReturn(ofNullable(clientOrder));
        ClientOrder clientOrderExpected = new ClientOrder();
        clientOrderExpected.setId(uuid);

        ClientOrder clientOrderActual = clientOrderService.findById(uuid);

        verify(clientOrderRepository).findById(uuid);
        assertEquals(clientOrderExpected.getId(), clientOrderActual.getId());
    }

    @Test
    public void testFindByIdWhenNotExist() {
        UUID uuid = UUID.fromString("5ad8e8a5-5973-4c75-b0f3-c3c29428fa1a");
        when(clientOrderRepository.findById(uuid)).thenReturn(empty());

        ClientOrder clientOrderActual = clientOrderService.findById(uuid);

        verify(clientOrderRepository).findById(uuid);
        assertNull(clientOrderActual);
    }

    @Test
    public void markAsPaid() {
        UUID uuid = UUID.fromString("5ad8e8a5-5973-4c75-b0f3-c3c29428fa1a");
        ClientOrder clientOrder = new ClientOrder();
        clientOrder.setId(uuid);

        when(clientOrderRepository.findById(uuid)).thenReturn(ofNullable(clientOrder));
        doNothing().when(clientOrderRepository).save(clientOrder);

        clientOrderService.markAsPaid(uuid);

        verify(clientOrderRepository).save(clientOrder);
        verify(clientOrderRepository).findById(uuid);

        assertEquals(clientOrder.getStatus(), "PAID");
    }

    @Test
    public void markAsPaidWhenClientOrderNotFound() {
        UUID uuid = UUID.fromString("5ad8e8a5-5973-4c75-b0f3-c3c29428fa1a");
        ClientOrder clientOrder = new ClientOrder();
        clientOrder.setId(uuid);

        when(clientOrderRepository.findById(uuid)).thenReturn(empty());
        doNothing().when(clientOrderRepository).save(clientOrder);

        try {
            clientOrderService.markAsPaid(uuid);
            fail("No RuntimeException");
        } catch (RuntimeException e) {

        }

        verify(clientOrderRepository, never()).save(clientOrder);
        verify(clientOrderRepository).findById(uuid);

        assertNull(clientOrder.getStatus());
    }

    private Product createProduct(UUID uuidProduct, Catalog catalog) {
        Product product = new Product();
        product.setId(uuidProduct);
        product.setCatalog(catalog);
        return product;
    }

    private Catalog createCatalog(int price) {
        Catalog catalog = new Catalog();
        catalog.setPrice(price);
        return catalog;
    }

    private ClientOrderItem createClientOrderItem(Product product, int quantity) {
        ClientOrderItem clientOrderItem = new ClientOrderItem();
        clientOrderItem.setProduct(product);
        clientOrderItem.setQuantity(quantity);
        return clientOrderItem;
    }
}