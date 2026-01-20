package com.yourcompany.pos.sales;

import com.yourcompany.pos.common.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SalesInvoiceRepository extends JpaRepository<SalesInvoice, UUID> {
    Optional<SalesInvoice> findByInvoiceNo(String invoiceNo);
    List<SalesInvoice> findByStatusAndTerminalCode(InvoiceStatus status, String terminalCode);
}
