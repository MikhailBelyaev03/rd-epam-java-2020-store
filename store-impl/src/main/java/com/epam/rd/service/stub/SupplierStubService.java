package com.epam.rd.service.stub;

import java.util.UUID;

/**
 * @author Belousov Anton
 * @{code SupplierStubService} always return immutable UUID. This class use like stub for call Supplier microservice
 */
public class SupplierStubService {

    /**
     * Give static UUID
     *
     * @return immutable UUID
     */
    public UUID send() {
        final UUID uuid = UUID.fromString("96d989e7-3d64-4e72-ab06-b3ec52f31f99");
        return uuid;
    }
}
