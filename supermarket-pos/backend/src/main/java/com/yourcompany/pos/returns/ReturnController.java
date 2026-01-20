package com.yourcompany.pos.returns;

import com.yourcompany.pos.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/returns")
@RequiredArgsConstructor
public class ReturnController {

    private final ReturnService returnService;

    @PostMapping
    public ApiResponse<ReturnResponse> create(@Valid @RequestBody ReturnRequest request) {
        return ApiResponse.ok(returnService.createReturn(request));
    }
}
