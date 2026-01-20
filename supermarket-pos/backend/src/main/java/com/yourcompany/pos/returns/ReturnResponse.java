package com.yourcompany.pos.returns;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReturnResponse {
    private String returnNo;
    private double totalAmount;
}
