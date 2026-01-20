package com.yourcompany.pos.sales;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SalesDetailResponse {
    private SalesInvoice invoice;
    private List<SalesItem> items;
    private List<SalesPayment> payments;
}
