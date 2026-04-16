package com.limasantos.pharmacy.api.financial.controller;

import com.limasantos.pharmacy.api.financial.dto.CreateFinancialDTO;
import com.limasantos.pharmacy.api.financial.dto.FinancialDTO;
import com.limasantos.pharmacy.api.financial.dto.FinancialDetailDTO;
import com.limasantos.pharmacy.api.financial.dto.FinancialSummaryDTO;
import com.limasantos.pharmacy.api.financial.service.FinancialService;
import com.limasantos.pharmacy.api.shared.enums.PaymentMethod;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/financial")
public class
FinancialController {

    private final FinancialService financialService;

    public FinancialController(FinancialService financialService) {
        this.financialService = financialService;
    }

    @PostMapping
    public ResponseEntity<FinancialDTO> create(@Valid @RequestBody CreateFinancialDTO dto) {
        FinancialDTO created = financialService.createFinancial(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FinancialDTO> findById(@PathVariable Long id) {
        FinancialDTO financial = financialService.findById(id);
        return ResponseEntity.ok(financial);
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<FinancialDetailDTO> findDetailById(@PathVariable Long id) {
        FinancialDetailDTO detail = financialService.findDetailById(id);
        return ResponseEntity.ok(detail);
    }

    @GetMapping
    public ResponseEntity<List<FinancialDTO>> findAll() {
        List<FinancialDTO> financials = financialService.findAll();
        return ResponseEntity.ok(financials);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<FinancialDTO>> findPending() {
        List<FinancialDTO> pending = financialService.findPending();
        return ResponseEntity.ok(pending);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<FinancialDTO>> findOverdue() {
        List<FinancialDTO> overdue = financialService.findOverdue();
        return ResponseEntity.ok(overdue);
    }

    @GetMapping("/receivable")
    public ResponseEntity<List<FinancialDTO>> findReceivable() {
        List<FinancialDTO> receivable = financialService.findReceivable();
        return ResponseEntity.ok(receivable);
    }

    @GetMapping("/payable")
    public ResponseEntity<List<FinancialDTO>> findPayable() {
        List<FinancialDTO> payable = financialService.findPayable();
        return ResponseEntity.ok(payable);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FinancialDTO> update(@PathVariable Long id, @Valid @RequestBody CreateFinancialDTO dto) {
        FinancialDTO updated = financialService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<FinancialDTO> markAsPaid(
            @PathVariable Long id,
            @RequestParam PaymentMethod paymentMethod) {
        FinancialDTO paid = financialService.markAsPaid(id, paymentMethod);
        return ResponseEntity.ok(paid);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        financialService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary")
    public ResponseEntity<FinancialSummaryDTO> generateSummary() {
        FinancialSummaryDTO summary = financialService.generateSummary();
        return ResponseEntity.ok(summary);
    }
}
