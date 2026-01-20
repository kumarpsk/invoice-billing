package com.yourcompany.pos.returns;

import com.yourcompany.pos.common.PaymentMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ReturnRequest {
    @NotBlank
    private String invoiceNo;
    @NotNull
    private List<ReturnItemRequest> items;
    @NotNull
    private PaymentMode refundMode;
    private String refundRef;
}
