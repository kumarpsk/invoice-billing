package com.yourcompany.pos.inventory;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PurchaseResponse {
    private String purchaseNo;
    private double totalAmount;
}
