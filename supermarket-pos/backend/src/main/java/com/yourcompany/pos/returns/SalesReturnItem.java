package com.yourcompany.pos.returns;

import com.yourcompany.pos.master.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "sales_return_item")
@Data
@NoArgsConstructor
public class SalesReturnItem {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "return_id")
    private SalesReturn salesReturn;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private double qty;
    private double amount;
}
