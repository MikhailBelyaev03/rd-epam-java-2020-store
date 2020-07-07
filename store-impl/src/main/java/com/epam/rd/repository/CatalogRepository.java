package com.epam.rd.repository;

import com.epam.rd.entity.Catalog;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

/**
 * Catalog repository for working with catalog table in database
 *
 * @author YurchenkoDD
 */
public interface CatalogRepository extends CrudRepository<Catalog, UUID> {
}
