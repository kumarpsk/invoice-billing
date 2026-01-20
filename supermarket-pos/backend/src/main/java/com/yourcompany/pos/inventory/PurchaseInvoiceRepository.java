package com.yourcompany.pos.inventory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PurchaseInvoiceRepository extends JpaRepository<PurchaseInvoice, UUID> {
    List<PurchaseInvoice> findByInvoiceDateBetween(LocalDate from, LocalDate to);
}
