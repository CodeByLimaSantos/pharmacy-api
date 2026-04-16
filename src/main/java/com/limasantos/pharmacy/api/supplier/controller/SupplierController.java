package com.limasantos.pharmacy.api.supplier.controller;

import com.limasantos.pharmacy.api.supplier.dto.CreateSupplierDTO;
import com.limasantos.pharmacy.api.supplier.dto.SupplierDTO;
import com.limasantos.pharmacy.api.supplier.dto.SupplierDetailDTO;
import com.limasantos.pharmacy.api.supplier.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @PostMapping
    public ResponseEntity<SupplierDTO> create(@Valid @RequestBody CreateSupplierDTO dto) {
        SupplierDTO created = supplierService.createSupplier(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierDTO> findById(@PathVariable Long id) {
        SupplierDTO supplier = supplierService.findById(id);
        return ResponseEntity.ok(supplier);
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<SupplierDetailDTO> findDetailById(@PathVariable Long id) {
        SupplierDetailDTO detail = supplierService.findDetailById(id);
        return ResponseEntity.ok(detail);
    }

    @GetMapping
    public ResponseEntity<List<SupplierDTO>> findAll() {
        List<SupplierDTO> suppliers = supplierService.findAll();
        return ResponseEntity.ok(suppliers);
    }

    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<SupplierDTO> findByCnpj(@PathVariable String cnpj) {
        SupplierDTO supplier = supplierService.findByCnpj(cnpj);
        return ResponseEntity.ok(supplier);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierDTO> update(@PathVariable Long id, @Valid @RequestBody CreateSupplierDTO dto) {
        SupplierDTO updated = supplierService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        supplierService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
