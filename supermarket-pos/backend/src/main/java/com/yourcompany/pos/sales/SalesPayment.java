package com.yourcompany.pos.sales;

import com.yourcompany.pos.common.PaymentMode;
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

import java.util.UUID;

@Entity
@Table(name = "sales_payment")
@Data
@NoArgsConstructor
public class SalesPayment {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "sales_id")
    private SalesInvoice salesInvoice;

    @Enumerated(EnumType.STRING)
    private PaymentMode mode;

    private double amount;
    private String refNo;
}
