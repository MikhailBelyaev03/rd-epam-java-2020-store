package com.epam.rd.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

/**
 * {@code SupplierOrderItem} describe table st_supplier_order_items in DB
 *
 * @author Belousov Anton
 */
@Entity
@Table(name = "st_supplier_order_items")
@NoArgsConstructor
@Data
public class SupplierOrderItem {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @Column(name = "product_id")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "order_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private SupplierOrder supplierOrder;

    private long quantity;
}
