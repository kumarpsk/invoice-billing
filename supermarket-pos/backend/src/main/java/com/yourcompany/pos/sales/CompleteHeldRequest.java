package com.yourcompany.pos.sales;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CompleteHeldRequest {
    @NotNull
    private List<SalesPaymentRequest> payments;
}
