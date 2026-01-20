package com.yourcompany.pos.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShiftOpenRequest {
    @NotBlank
    private String terminalCode;
    @NotNull
    private double openingCash;
}
