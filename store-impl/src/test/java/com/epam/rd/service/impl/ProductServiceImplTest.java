package com.epam.rd.service.impl;

import com.epam.rd.entity.Catalog;
import com.epam.rd.entity.Product;
import com.epam.rd.entity.SupplierOrder;
import com.epam.rd.repository.CatalogRepository;
import com.epam.rd.repository.ProductRepository;
import com.epam.rd.repository.SupplierOrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doNothing;

/**
 * {@code ProductServiceImplTest} testing work {@link ProductServiceImpl}
 *
 * @author Belousov Anton
 */
@RunWith(MockitoJUnitRunner.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository = new ProductRepository();

    @Mock
    private SupplierOrderServiceImpl supplierOrderService = new SupplierOrderServiceImpl();

    @Mock
    private CatalogRepository catalogRepository = new CatalogRepository();

    @Mock
    private SupplierOrderRepository supplierOrderRepository = new SupplierOrderRepository();

    @InjectMocks
    private ProductServiceImpl productService = new ProductServiceImpl();

    @Test
    public void testCalculateStockWhenMapNotEmpty() {

        Map<UUID, Integer> map = new HashMap<>();

        when(productRepository.findAll()).thenReturn(createProductWhenQuantityEleven());

        productService.calculateStock();

        assertNotNull(map);
    }

    @Test
    public void testCalculateStockWhenMapEmpty() {

        when(productRepository.findAll()).thenReturn(createProductWhenQuantityOneHundred());

        Map<UUID, Integer> map = null;

        try {
            productService.calculateStock();
            fail("No Runtime Exception");
        } catch (RuntimeException e) {

        }
        verify(supplierOrderService, never()).create(map);
        assertNull(map);
    }

    @Test
    public void testReceiveDeliveryWhenDeliveredSupplierOrderIdExists() {

        UUID uuid = UUID.fromString("96d989e7-3d64-4e72-ab06-b3ec52f31f99");
        SupplierOrder supplierOrder = new SupplierOrder();
        supplierOrder.setId(uuid);

        when(supplierOrderRepository.findById(uuid)).thenReturn(of(supplierOrder));

        Product product = createProduct();

        Integer oldQuantity = product.getCatalog().getQuantity();

        doNothing().when(supplierOrderRepository).save(supplierOrder);
        productService.receiveDelivery(supplierOrder.getId());

        assertNotEquals(of(product.getCatalog().getQuantity()), oldQuantity);

        verify(supplierOrderService).markAsDelivered(supplierOrder.getId());
    }

    @Test
    public void testReceiveDeliveryWhenDeliveredSupplierOrderIdNotExists() {

        UUID uuid = UUID.fromString("96d989e7-3d64-4e72-ab06-b3ec52f31f99");
        SupplierOrder supplierOrder = new SupplierOrder();
        supplierOrder.setId(uuid);

        when(supplierOrderRepository.findById(uuid)).thenReturn(empty());

        try {
            productService.receiveDelivery(uuid);
            fail("No RuntimeException");
        } catch (RuntimeException e) {

        }

        verify(supplierOrderService, never()).markAsDelivered(uuid);
    }

    @Test
    public void testReserveProductWhenProductExists() {

        UUID uuid = UUID.fromString("0afd3797-f753-4aed-94a1-0c7a0a053d21");

        Product product = createProduct();
        when(productRepository.findById(uuid)).thenReturn(of(product));
        Integer oldQuantity = product.getCatalog().getQuantity();

        doNothing().when(catalogRepository).save(product.getCatalog());

        productService.reserveProduct(product.getId(), product.getCatalog().getQuantity());
        verify(catalogRepository).save(product.getCatalog());

        assertNotEquals(of(product.getCatalog().getQuantity()), oldQuantity);
    }

    @Test
    public void testReserveProductWhenProductNotExists() {

        UUID uuid = UUID.fromString("0afd3797-f753-4aed-94a1-0c7a0a053d21");

        Product product = createProduct();
        when(productRepository.findById(uuid)).thenReturn(empty());
        doNothing().when(catalogRepository).save(product.getCatalog());

        try {
            productService.reserveProduct(product.getId(), product.getCatalog().getQuantity());
            fail("No RuntimeException");
        } catch (RuntimeException e) {

        }
    }

    private List<Product> createProductWhenQuantityEleven() {
        List<Product> productList1 = new ArrayList<>();
        Product product1 = new Product();
        product1.setId(UUID.fromString("0afd3797-f753-4aed-94a1-0c7a0a053d21"));

        Catalog catalog1 = new Catalog();
        catalog1.setPrice(1);
        catalog1.setQuantity(11);

        product1.setCatalog(catalog1);

        productList1.add(product1);

        return productList1;
    }

    private List<Product> createProductWhenQuantityOneHundred() {
        List<Product> productList = new ArrayList<>();
        Product product2 = new Product();
        product2.setId(UUID.fromString("0afd3797-f753-4aed-94a1-0c7a0a053d21"));

        Catalog catalog2 = new Catalog();
        catalog2.setPrice(1);
        catalog2.setQuantity(100);

        product2.setCatalog(catalog2);

        productList.add(product2);

        return productList;
    }

    private Product createProduct() {
        Product product = new Product();

        product.setId(UUID.fromString("0afd3797-f753-4aed-94a1-0c7a0a053d21"));

        Catalog catalog = new Catalog();
        catalog.setPrice(1);
        catalog.setQuantity(100);

        product.setCatalog(catalog);

        return product;
    }
}