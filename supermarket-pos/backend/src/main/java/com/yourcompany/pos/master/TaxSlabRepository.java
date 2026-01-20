package com.yourcompany.pos.master;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaxSlabRepository extends JpaRepository<TaxSlab, UUID> {
}
