package com.yourcompany.pos.sales;

import com.yourcompany.pos.auth.CashRegisterShift;
import com.yourcompany.pos.common.InvoiceStatus;
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
@Table(name = "sales_invoice")
@Data
@NoArgsConstructor
public class SalesInvoice {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    private String invoiceNo;
    private String terminalCode;

    @ManyToOne
    @JoinColumn(name = "shift_id")
    private CashRegisterShift shift;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status = InvoiceStatus.COMPLETED;

    private LocalDateTime billDatetime = LocalDateTime.now();
    private String customerName;
    private String customerPhone;

    private double subtotal;
    private double discountTotal;
    private double taxableTotal;
    private double cgstTotal;
    private double sgstTotal;
    private double roundOff;
    private double grandTotal;
}
