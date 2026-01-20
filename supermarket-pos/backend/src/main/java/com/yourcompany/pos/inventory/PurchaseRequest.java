package com.yourcompany.pos.inventory;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class PurchaseRequest {
    @NotNull
    private UUID vendorId;
    @NotNull
    private LocalDate invoiceDate;
    @NotNull
    private List<PurchaseItemRequest> items;
}
