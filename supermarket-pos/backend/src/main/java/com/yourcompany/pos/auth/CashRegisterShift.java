package com.yourcompany.pos.auth;

import com.yourcompany.pos.common.ShiftStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cash_register_shift")
@Data
@NoArgsConstructor
public class CashRegisterShift {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "terminal_code", nullable = false)
    private String terminalCode;

    @Enumerated(EnumType.STRING)
    private ShiftStatus status = ShiftStatus.OPEN;

    @Column(name = "opening_cash", nullable = false)
    private double openingCash;

    @Column(name = "closing_cash")
    private Double closingCash;

    private String remarks;

    @Column(name = "opened_at")
    private LocalDateTime openedAt = LocalDateTime.now();

    @Column(name = "closed_at")
    private LocalDateTime closedAt;
}
