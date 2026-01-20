package com.yourcompany.pos.master;

import com.yourcompany.pos.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;

    @GetMapping
    public ApiResponse<List<Product>> search(@RequestParam(required = false) String barcode,
                                             @RequestParam(required = false) String name) {
        if (barcode != null && !barcode.isBlank()) {
            return ApiResponse.ok(productRepository.findByBarcode(barcode).map(List::of).orElse(List.of()));
        }
        if (name != null && !name.isBlank()) {
            return ApiResponse.ok(productRepository.findByNameContainingIgnoreCase(name));
        }
        return ApiResponse.ok(productRepository.findAll());
    }
}
