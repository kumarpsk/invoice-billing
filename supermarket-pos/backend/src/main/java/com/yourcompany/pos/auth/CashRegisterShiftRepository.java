package com.yourcompany.pos.auth;

import com.yourcompany.pos.common.ShiftStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CashRegisterShiftRepository extends JpaRepository<CashRegisterShift, UUID> {
    Optional<CashRegisterShift> findByTerminalCodeAndStatus(String terminalCode, ShiftStatus status);
}
