package com.epam.rd.entity;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

/**
 * {@code SupplierOrderItem} describe table st_supplier_order_items in DB
 *
 * @author Belousov Anton
 */
@Entity
@Table(name = "st_supplier_order_items")
@ToString(exclude = "supplierOrder")
@EqualsAndHashCode(exclude = "supplierOrder")
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private SupplierOrder supplierOrder;

    private long quantity;
}
