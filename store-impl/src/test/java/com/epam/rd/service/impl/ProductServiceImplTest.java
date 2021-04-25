package com.epam.rd.service.impl;

import static java.util.Optional.of;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.epam.rd.Application;
import com.epam.rd.entity.Catalog;
import com.epam.rd.entity.Product;
import com.epam.rd.repository.CatalogRepository;
import com.epam.rd.repository.ProductRepository;
import com.epam.rd.repository.SupplierOrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ProductServiceImplTest {

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private SupplierOrderServiceImpl supplierOrderService;

    @MockBean
    private SupplierOrderRepository supplierOrderRepository;

    @MockBean
    private CatalogRepository catalogRepository;

    @InjectMocks
    @Autowired
    private ProductServiceImpl productService;

    @Test
    public void calculateStockWhenRepositoryIsNotEmpty() {
        Map<UUID, Integer> productMap = new HashMap<>();
        UUID expectedUUID = UUID.randomUUID();
        productMap.put(expectedUUID, 1);

        productService.calculateStock();

        verify(supplierOrderService).create(anyMap());
    }

    @Test(expected = RuntimeException.class)
    public void receiveDeliveryWhenSupplierOrderNullThenThrowException() {

        UUID expectedUUID = UUID.randomUUID();
        supplierOrderService.getSupplierOrderRepository().findById(expectedUUID);
        verify(supplierOrderRepository).findById(expectedUUID);
    }

    @Test(expected = RuntimeException.class)
    public void receiveDeliveryWhenSupplierOrderNotNullThenMarkAsDelivered() {

        UUID expectedUUID = UUID.randomUUID();
        supplierOrderService.getSupplierOrderRepository().findById(expectedUUID);
        verify(supplierOrderRepository).findById(expectedUUID);

        supplierOrderService.markAsDelivered(expectedUUID);
        assertEquals("Delivered", supplierOrderRepository.findById(expectedUUID).get().getStatus());
    }


    @Test
    public void reserveProductWhenProductNotFoundedThenThrowException() {
        UUID expectedUUID = UUID.randomUUID();
        when(productService.getSupplierOrderRepository().findByPaymentId(expectedUUID)).thenReturn(null);

        try {
            productRepository.findById(expectedUUID);
        } catch (RuntimeException re) {
            String message = "Product not found by product id";
            assertEquals(message, re.getMessage());
        }

    }

    @Test
    public void reserveProductWhenProductIsFoundedThenSaveToTheRepository() {
        Catalog actualCatalog = new Catalog();
        UUID UUIDCatalog = UUID.randomUUID();
        actualCatalog.setId(UUIDCatalog);

        UUID UUIDProduct = UUID.randomUUID();
        Product expectedProduct = new Product();
        expectedProduct.setId(UUIDProduct);
        expectedProduct.setCatalog(actualCatalog);
        expectedProduct.getCatalog().setQuantity(15);

        when(productRepository.findById(UUIDProduct)).thenReturn(of(expectedProduct));
        productService.reserveProduct(UUIDProduct, 10);
        verify(catalogRepository).save(actualCatalog);
    }

    @Test
    public void reserveProductWhenQuantityProductNotEnoughThenThrowException() {

        UUID expectedUUID = UUID.randomUUID();
        Product product = new Product();
        product.setId(expectedUUID);
        Catalog catalog = new Catalog();
        catalog.setQuantity(100);
        product.setCatalog(catalog);
        when(productRepository.findById(expectedUUID)).thenReturn(of(product));

        try {
            productService.reserveProduct(expectedUUID, 10);
        } catch (RuntimeException re) {
            String message = "The quantity of goods in stock is less than necessary for the order";
            assertEquals(message, re.getMessage());
        }


    }

}