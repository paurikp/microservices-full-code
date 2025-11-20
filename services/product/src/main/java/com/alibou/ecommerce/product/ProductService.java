package com.alibou.ecommerce.product;

import com.alibou.ecommerce.exception.ProductPurchaseException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository repository;
	private final ProductMapper mapper;

	public ProductResponse createProduct(ProductRequest request) {
		var product = this.repository.save(mapper.toProduct(request));
		return this.mapper.toProductResponse(product);
	}

    public ProductResponse updateProduct(Integer productId, ProductRequest request) {
        var product = this.repository.findById(productId).orElseThrow(() -> new EntityNotFoundException(
                "Cannot update product:: No product found with the provided ID: " + productId));
        mergeProduct(product, request);
        this.repository.save(product);
        return this.mapper.toProductResponse(product);
    }
    private void mergeProduct(Product product, ProductRequest request) {
        if (request.name() != null && !request.name().isBlank()) {
            product.setName(request.name());
        }

        if (request.description() != null && !request.description().isBlank()) {
            product.setDescription(request.description());
        }

        if (request.price() != null) {
            product.setPrice(request.price());
        }

        if (String.valueOf(request.availableQuantity()) != null) {
            product.setAvailableQuantity(request.availableQuantity());
        }
    }

	public ProductResponse findById(Integer id) {
		return repository.findById(id).map(mapper::toProductResponse)
				.orElseThrow(() -> new EntityNotFoundException("Product not found with ID:: " + id));
	}

	public List<ProductResponse> findAll() {
		return repository.findAll().stream().map(mapper::toProductResponse).collect(Collectors.toList());
	}

	@Transactional(rollbackFor = ProductPurchaseException.class)
	public List<ProductPurchaseResponse> purchaseProducts(List<ProductPurchaseRequest> request) {
		var productIds = request.stream().map(ProductPurchaseRequest::productId).toList();
		var storedProducts = repository.findAllByIdInOrderById(productIds);
		if (productIds.size() != storedProducts.size()) {
			throw new ProductPurchaseException("One or more products does not exist");
		}
		var sortedRequest = request.stream().sorted(Comparator.comparing(ProductPurchaseRequest::productId)).toList();
		var purchasedProducts = new ArrayList<ProductPurchaseResponse>();
		for (int i = 0; i < storedProducts.size(); i++) {
			var product = storedProducts.get(i);
			var productRequest = sortedRequest.get(i);
			if (product.getAvailableQuantity() < productRequest.quantity()) {
				throw new ProductPurchaseException(
						"Insufficient stock quantity for product with ID:: " + productRequest.productId());
			}
			var newAvailableQuantity = product.getAvailableQuantity() - productRequest.quantity();
			product.setAvailableQuantity(newAvailableQuantity);
			repository.save(product);
			purchasedProducts.add(mapper.toproductPurchaseResponse(product, productRequest.quantity()));
		}
		return purchasedProducts;
	}

    public void deleteProduct(Integer productId) {
        this.repository.deleteById(productId);
    }

}
