package com.yourcompany.pos.inventory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PurchaseItemRepository extends JpaRepository<PurchaseItem, UUID> {
}
