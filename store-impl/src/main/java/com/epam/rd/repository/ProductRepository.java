package com.epam.rd.repository;

import com.epam.rd.entity.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

/**
 * Product repository for working with product table in database
 *
 * @author YurchenkoDD
 */

public interface ProductRepository extends CrudRepository<Product, UUID> {
}
