package com.yourcompany.pos.sales;

import com.yourcompany.pos.common.PaymentMode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SalesPaymentRequest {
    @NotNull
    private PaymentMode mode;
    private double amount;
    private String refNo;
}
