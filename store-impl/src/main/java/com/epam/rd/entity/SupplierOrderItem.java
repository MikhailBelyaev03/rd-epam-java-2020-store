package com.epam.rd.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "supplier_order_items")
@NoArgsConstructor
@Getter
@Setter
public class SupplierOrderItem {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private SupplierOrder supplierOrder;

    private long quantity;

    @Override
    public String toString() {
        return "SupplierOrderItem{" +
                "id=" + id +
                ", product=" + product +
                ", supplierOrder=" + supplierOrder +
                ", quantity=" + quantity +
                '}';
    }
}
