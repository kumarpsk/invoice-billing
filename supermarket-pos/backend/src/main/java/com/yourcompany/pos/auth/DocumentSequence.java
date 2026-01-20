package com.yourcompany.pos.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "document_sequence")
@Data
@NoArgsConstructor
public class DocumentSequence {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "doc_type", nullable = false)
    private String docType;

    @Column(name = "fiscal_year", nullable = false)
    private String fiscalYear;

    @Column(name = "next_number", nullable = false)
    private long nextNumber = 1;
}
