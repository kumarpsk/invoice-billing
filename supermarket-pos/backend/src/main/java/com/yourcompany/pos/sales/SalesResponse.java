package com.yourcompany.pos.sales;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SalesResponse {
    private String salesId;
    private String invoiceNo;
    private double grandTotal;
    private List<SalesPaymentRequest> paymentBreakdown;
    private Object printPayload;
}
