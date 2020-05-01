package com.epam.rd.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * {@code SupplierOrder} describe table st_supplier_order in DB
 *
 * @author Belousov Anton
 */
@Entity
@Table(name = "st_supplier_order")
@NoArgsConstructor
@Data
public class SupplierOrder {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    private long amount;

    private String status;

    @Column(name = "payment_callback_url")
    private String paymentCallbackUrl;

    @Column(name = "payment_id")
    private UUID paymentId;

    @OneToMany(mappedBy = "supplierOrder", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<SupplierOrderItem> supplierOrderItem = new ArrayList<>();
}
