package com.epam.rd.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "st_client_order")
@NoArgsConstructor
@Data
public class ClientOrder {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    private long amount;

    private String status;

    @Column(name = "payment_id")
    private UUID paymentId;

    @OneToMany(mappedBy = "clientOrder", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<ClientOrderItem> clientOrderItem;

}
