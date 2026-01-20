package com.yourcompany.pos.sales;

import com.yourcompany.pos.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SalesController {

    private final SalesService salesService;

    @PostMapping
    public ApiResponse<SalesResponse> create(@Valid @RequestBody SalesRequest request) {
        return ApiResponse.ok(salesService.createSale(request));
    }

    @PostMapping("/hold")
    public ApiResponse<SalesResponse> hold(@Valid @RequestBody SalesHoldRequest request) {
        return ApiResponse.ok(salesService.holdSale(request));
    }

    @GetMapping("/held")
    public ApiResponse<List<SalesInvoice>> held(@RequestParam String terminalCode) {
        return ApiResponse.ok(salesService.heldSales(terminalCode));
    }

    @PostMapping("/{id}/resume")
    public ApiResponse<SalesDetailResponse> resume(@PathVariable UUID id) {
        return ApiResponse.ok(salesService.getSalesDetail(id));
    }

    @PostMapping("/{id}/complete")
    public ApiResponse<SalesResponse> completeHeld(@PathVariable UUID id,
                                                   @Valid @RequestBody CompleteHeldRequest request) {
        return ApiResponse.ok(salesService.completeHeld(id, request));
    }

    @GetMapping("/{id}")
    public ApiResponse<SalesDetailResponse> getById(@PathVariable UUID id) {
        return ApiResponse.ok(salesService.getSalesDetail(id));
    }

    @GetMapping("/by-invoice")
    public ApiResponse<SalesDetailResponse> getByInvoice(@RequestParam String invoiceNo) {
        return ApiResponse.ok(salesService.getByInvoiceNo(invoiceNo));
    }
}
