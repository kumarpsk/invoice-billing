package com.yourcompany.pos.sales;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class SalesItemRequest {
    @NotNull
    private UUID productId;
    private double qty;
    private double discountAmount;
}
