package com.yourcompany.pos.auth;

import com.yourcompany.pos.common.ShiftStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ShiftResponse {
    private UUID id;
    private String terminalCode;
    private ShiftStatus status;
    private double openingCash;
    private Double closingCash;
    private String remarks;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
}
