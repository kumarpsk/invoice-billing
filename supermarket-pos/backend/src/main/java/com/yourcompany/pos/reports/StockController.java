package com.yourcompany.pos.reports;

import com.yourcompany.pos.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {

    private final ReportsService reportsService;

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> stock() {
        return ApiResponse.ok(reportsService.stock());
    }

    @GetMapping("/low")
    public ApiResponse<List<Map<String, Object>>> lowStock() {
        return ApiResponse.ok(reportsService.lowStock());
    }
}
