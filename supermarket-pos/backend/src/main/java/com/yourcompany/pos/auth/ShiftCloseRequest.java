package com.yourcompany.pos.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ShiftCloseRequest {
    @NotNull
    private UUID shiftId;
    private double closingCash;
    private String remarks;
}
