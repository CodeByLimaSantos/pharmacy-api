package com.limasantos.pharmacy.api.customer.controller;

import com.limasantos.pharmacy.api.customer.dto.CreateCustomerDTO;
import com.limasantos.pharmacy.api.customer.dto.CustomerDTO;
import com.limasantos.pharmacy.api.customer.dto.CustomerDetailDTO;
import com.limasantos.pharmacy.api.customer.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerDTO> create(@Valid @RequestBody CreateCustomerDTO dto) {
        CustomerDTO created = customerService.createCustomer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> findById(@PathVariable Long id) {
        CustomerDTO customer = customerService.findById(id);
        return ResponseEntity.ok(customer);
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<CustomerDetailDTO> findDetailById(@PathVariable Long id) {
        CustomerDetailDTO detail = customerService.findDetailById(id);
        return ResponseEntity.ok(detail);
    }

    @GetMapping
    public ResponseEntity<List<CustomerDTO>> findAll() {
        List<CustomerDTO> customers = customerService.findAll();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<CustomerDTO> findByCpf(@PathVariable String cpf) {
        CustomerDTO customer = customerService.findByCpf(cpf);
        return ResponseEntity.ok(customer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> update(@PathVariable Long id, @Valid @RequestBody CreateCustomerDTO dto) {
        CustomerDTO updated = customerService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
