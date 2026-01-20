package com.yourcompany.pos.auth;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface DocumentSequenceRepository extends JpaRepository<DocumentSequence, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ds from DocumentSequence ds where ds.docType = :docType and ds.fiscalYear = :fiscalYear")
    Optional<DocumentSequence> findForUpdate(@Param("docType") String docType, @Param("fiscalYear") String fiscalYear);
}
