package com.limasantos.pharmacy.api.sales.controller;

import com.limasantos.pharmacy.api.sales.dto.CreateSaleDTO;
import com.limasantos.pharmacy.api.sales.dto.SaleDTO;
import com.limasantos.pharmacy.api.sales.dto.SaleDetailDTO;
import com.limasantos.pharmacy.api.sales.service.SaleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping
    public ResponseEntity<SaleDTO> create(@Valid @RequestBody CreateSaleDTO dto) {
        SaleDTO created = saleService.createSale(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleDTO> findById(@PathVariable Long id) {
        SaleDTO sale = saleService.findById(id);
        return ResponseEntity.ok(sale);
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<SaleDetailDTO> findDetailById(@PathVariable Long id) {
        SaleDetailDTO detail = saleService.findDetailById(id);
        return ResponseEntity.ok(detail);
    }

    @GetMapping
    public ResponseEntity<List<SaleDTO>> findAll() {
        List<SaleDTO> sales = saleService.findAll();
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<SaleDTO>> findByCustomer(@PathVariable Long customerId) {
        List<SaleDTO> sales = saleService.findByCustomer(customerId);
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/total/amount")
    public ResponseEntity<BigDecimal> getTotalSalesAmount() {
        BigDecimal total = saleService.getTotalSalesAmount();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/customer/{customerId}/total")
    public ResponseEntity<BigDecimal> getCustomerTotalSales(@PathVariable Long customerId) {
        BigDecimal total = saleService.getCustomerTotalSales(customerId);
        return ResponseEntity.ok(total);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelSale(@PathVariable Long id) {
        saleService.cancelSale(id);
        return ResponseEntity.noContent().build();
    }
}
