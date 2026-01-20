package com.yourcompany.pos.returns;

import com.yourcompany.pos.common.PaymentMode;
import com.yourcompany.pos.sales.SalesInvoice;
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
@Table(name = "sales_return")
@Data
@NoArgsConstructor
public class SalesReturn {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    private String returnNo;

    @ManyToOne
    @JoinColumn(name = "sales_id")
    private SalesInvoice salesInvoice;

    @Enumerated(EnumType.STRING)
    private PaymentMode refundMode;

    private String refundRef;
    private double totalAmount;
    private LocalDateTime createdAt = LocalDateTime.now();
}
