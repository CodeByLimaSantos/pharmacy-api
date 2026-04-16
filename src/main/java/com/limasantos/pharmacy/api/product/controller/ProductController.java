package com.limasantos.pharmacy.api.product.controller;

import com.limasantos.pharmacy.api.product.dto.CreateProductDTO;
import com.limasantos.pharmacy.api.product.dto.ProductDTO;
import com.limasantos.pharmacy.api.product.dto.ProductDetailDTO;
import com.limasantos.pharmacy.api.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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

    @PostMapping
    public ResponseEntity<ProductDTO> create(@Valid @RequestBody CreateProductDTO dto) {
        ProductDTO created = productService.createProduct(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> findById(@PathVariable Long id) {
        ProductDTO product = productService.findById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<ProductDetailDTO> findDetailById(@PathVariable Long id) {
        ProductDetailDTO detail = productService.findDetailById(id);
        return ResponseEntity.ok(detail);
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> findAll() {
        List<ProductDTO> products = productService.findAll();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<ProductDTO>> findBySupplier(@PathVariable Long supplierId) {
        List<ProductDTO> products = productService.findBySupplier(supplierId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/controlled")
    public ResponseEntity<List<ProductDTO>> findControlledProducts() {
        List<ProductDTO> products = productService.findControlledProducts();
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> update(@PathVariable Long id, @Valid @RequestBody CreateProductDTO dto) {
        ProductDTO updated = productService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
