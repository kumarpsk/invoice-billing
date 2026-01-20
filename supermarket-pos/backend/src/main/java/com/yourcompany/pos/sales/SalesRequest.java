package com.yourcompany.pos.sales;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SalesRequest {
    @NotBlank
    private String terminalCode;
    private String customerName;
    private String customerPhone;
    private double billDiscountAmount;
    @NotNull
    private List<SalesItemRequest> items;
    @NotNull
    private List<SalesPaymentRequest> payments;
}
