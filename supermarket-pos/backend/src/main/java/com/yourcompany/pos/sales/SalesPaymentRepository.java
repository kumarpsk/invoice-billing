package com.yourcompany.pos.sales;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface SalesPaymentRepository extends JpaRepository<SalesPayment, UUID> {
    List<SalesPayment> findBySalesInvoiceId(UUID salesId);
}
