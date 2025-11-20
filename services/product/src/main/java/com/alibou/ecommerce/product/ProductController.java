package com.alibou.ecommerce.product;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService service;

	@PostMapping
	public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid ProductRequest request) {
		return ResponseEntity.ok(service.createProduct(request));
	}

    @PutMapping("/{product-id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable("product-id") Integer productId,
                                                         @RequestBody @Valid ProductRequest request) {
        return ResponseEntity.ok(service.updateProduct(productId, request));
    }

	@PostMapping("/purchase")
	public ResponseEntity<List<ProductPurchaseResponse>> purchaseProducts(
			@RequestBody List<ProductPurchaseRequest> request) {
		return ResponseEntity.ok(service.purchaseProducts(request));
	}

	@GetMapping("/{product-id}")
	public ResponseEntity<ProductResponse> findById(@PathVariable("product-id") Integer productId) {
		return ResponseEntity.ok(service.findById(productId));
	}

	@GetMapping
	public ResponseEntity<List<ProductResponse>> findAll() {
		return ResponseEntity.ok(service.findAll());
	}

    @DeleteMapping("/{customer-id}")
    public ResponseEntity<Void> delete(@PathVariable("customer-id") Integer productId) {
        this.service.deleteProduct(productId);
        return ResponseEntity.accepted().build();
    }
}
