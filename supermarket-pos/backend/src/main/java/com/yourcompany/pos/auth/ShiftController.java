package com.yourcompany.pos.auth;

import com.yourcompany.pos.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
public class ShiftController {

    private final ShiftService shiftService;

    @PostMapping("/open")
    public ApiResponse<ShiftResponse> open(@Valid @RequestBody ShiftOpenRequest request) {
        return ApiResponse.ok(shiftService.openShift(request));
    }

    @GetMapping("/active")
    public ApiResponse<ShiftResponse> active(@RequestParam String terminalCode) {
        return ApiResponse.ok(shiftService.getActiveShift(terminalCode));
    }

    @PostMapping("/close")
    public ApiResponse<ShiftResponse> close(@Valid @RequestBody ShiftCloseRequest request) {
        return ApiResponse.ok(shiftService.closeShift(request));
    }
}
