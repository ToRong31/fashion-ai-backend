package com.torome.store.product;

import com.torome.store.product.dto.ProductListResponse;
import com.torome.store.product.dto.ProductResponse;
import com.torome.store.product.dto.VectorSearchRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/vector-search")
    public ResponseEntity<ProductListResponse> vectorSearch(@RequestBody VectorSearchRequest request) {
        return ResponseEntity.ok(productService.vectorSearch(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @GetMapping
    public ResponseEntity<ProductListResponse> listProducts() {
        return ResponseEntity.ok(productService.listProducts());
    }

    @PostMapping("/batch")
    public ResponseEntity<ProductListResponse> getProductsByIds(@RequestBody List<Long> ids) {
        return ResponseEntity.ok(productService.getProductsByIds(ids));
    }
}
