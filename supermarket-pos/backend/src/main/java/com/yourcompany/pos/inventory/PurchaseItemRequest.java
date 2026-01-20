package com.yourcompany.pos.inventory;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class PurchaseItemRequest {
    @NotNull
    private UUID productId;
    private double qty;
    private double costPrice;
}
