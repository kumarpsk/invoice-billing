package com.yourcompany.pos.returns;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SalesReturnRepository extends JpaRepository<SalesReturn, UUID> {
}
