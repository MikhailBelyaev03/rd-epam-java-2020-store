package com.epam.rd.repository;

import com.epam.rd.entity.ClientOrder;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ClientOrderRepository extends CrudRepository<ClientOrder, UUID> {
}
