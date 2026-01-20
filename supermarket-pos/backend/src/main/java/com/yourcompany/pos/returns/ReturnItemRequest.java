package com.yourcompany.pos.returns;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ReturnItemRequest {
    @NotNull
    private UUID productId;
    private double qty;
}
