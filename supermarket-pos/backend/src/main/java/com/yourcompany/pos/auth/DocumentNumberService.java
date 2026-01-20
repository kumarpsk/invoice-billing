package com.yourcompany.pos.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DocumentNumberService {

    private final DocumentSequenceRepository documentSequenceRepository;

    @Transactional
    public String nextSalesInvoice(LocalDate date) {
        return nextNumber("INV", date);
    }

    @Transactional
    public String nextReturn(LocalDate date) {
        return nextNumber("RET", date);
    }

    @Transactional
    public String nextPurchase(LocalDate date) {
        return nextNumber("PUR", date);
    }

    private String nextNumber(String docType, LocalDate date) {
        String fiscalYear = toFiscalYear(date);
        DocumentSequence seq = documentSequenceRepository.findForUpdate(docType, fiscalYear)
            .orElseGet(() -> {
                DocumentSequence created = new DocumentSequence();
                created.setDocType(docType);
                created.setFiscalYear(fiscalYear);
                created.setNextNumber(1);
                return documentSequenceRepository.save(created);
            });
        long current = seq.getNextNumber();
        seq.setNextNumber(current + 1);
        documentSequenceRepository.save(seq);
        return String.format("%s/%s/%06d", docType, fiscalYear, current);
    }

    private String toFiscalYear(LocalDate date) {
        int year = date.getYear();
        int startYear = date.getMonthValue() <= 3 ? year - 1 : year;
        int endYear = startYear + 1;
        return String.format("%d-%02d", startYear, endYear % 100);
    }
}
