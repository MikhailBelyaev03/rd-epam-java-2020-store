package com.epam.rd.repository;

import com.epam.rd.entity.ClientOrderItem;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;


public interface ClientOrderItemRepository extends CrudRepository<ClientOrderItem, UUID> {
}
