package com.yourcompany.pos.auth;

import com.yourcompany.pos.common.ShiftStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShiftService {

    private final CashRegisterShiftRepository shiftRepository;

    @Transactional
    public ShiftResponse openShift(ShiftOpenRequest request) {
        shiftRepository.findByTerminalCodeAndStatus(request.getTerminalCode(), ShiftStatus.OPEN)
            .ifPresent(existing -> {
                throw new IllegalArgumentException("Shift already open for terminal");
            });
        CashRegisterShift shift = new CashRegisterShift();
        shift.setTerminalCode(request.getTerminalCode());
        shift.setOpeningCash(request.getOpeningCash());
        shift.setStatus(ShiftStatus.OPEN);
        CashRegisterShift saved = shiftRepository.save(shift);
        return toResponse(saved);
    }

    public ShiftResponse getActiveShift(String terminalCode) {
        CashRegisterShift shift = shiftRepository.findByTerminalCodeAndStatus(terminalCode, ShiftStatus.OPEN)
            .orElseThrow(() -> new IllegalArgumentException("No active shift"));
        return toResponse(shift);
    }

    @Transactional
    public ShiftResponse closeShift(ShiftCloseRequest request) {
        CashRegisterShift shift = shiftRepository.findById(request.getShiftId())
            .orElseThrow(() -> new IllegalArgumentException("Shift not found"));
        if (shift.getStatus() == ShiftStatus.CLOSED) {
            throw new IllegalArgumentException("Shift already closed");
        }
        shift.setClosingCash(request.getClosingCash());
        shift.setRemarks(request.getRemarks());
        shift.setStatus(ShiftStatus.CLOSED);
        shift.setClosedAt(java.time.LocalDateTime.now());
        return toResponse(shiftRepository.save(shift));
    }

    private ShiftResponse toResponse(CashRegisterShift shift) {
        return new ShiftResponse(
            shift.getId(),
            shift.getTerminalCode(),
            shift.getStatus(),
            shift.getOpeningCash(),
            shift.getClosingCash(),
            shift.getRemarks(),
            shift.getOpenedAt(),
            shift.getClosedAt()
        );
    }
}
