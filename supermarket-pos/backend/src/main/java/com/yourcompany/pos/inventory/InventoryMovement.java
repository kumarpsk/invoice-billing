package com.yourcompany.pos.inventory;

import com.yourcompany.pos.common.InventoryTxnType;
import com.yourcompany.pos.master.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory_movement")
@Data
@NoArgsConstructor
public class InventoryMovement {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Enumerated(EnumType.STRING)
    private InventoryTxnType txnType;

    private double qtyIn;
    private double qtyOut;
    private String refNo;
    private LocalDateTime createdAt = LocalDateTime.now();
}
