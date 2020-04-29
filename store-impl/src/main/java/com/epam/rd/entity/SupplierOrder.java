package com.epam.rd.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "st_supplier_order")
@NoArgsConstructor
@Getter
@Setter
public class SupplierOrder {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    private long amount;
    private String status;
    private String payment_callback_url;
    private UUID payment_id;

    @OneToMany(mappedBy = "supplier_order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SupplierOrderItem> supplierOrderItem = new ArrayList<>();

    @Override
    public String toString() {
        return "SupplierOrder{" +
                "id=" + id +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", payment_callback_url='" + payment_callback_url + '\'' +
                ", payment_id=" + payment_id +
                '}';
    }
}
