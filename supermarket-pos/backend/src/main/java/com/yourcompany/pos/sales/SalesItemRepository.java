package com.yourcompany.pos.sales;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface SalesItemRepository extends JpaRepository<SalesItem, UUID> {
    List<SalesItem> findBySalesInvoiceId(UUID salesId);
}
