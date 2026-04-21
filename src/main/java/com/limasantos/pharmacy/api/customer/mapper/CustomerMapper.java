package com.limasantos.pharmacy.api.customer.mapper;

import com.limasantos.pharmacy.api.customer.dto.CreateCustomerDTO;
import com.limasantos.pharmacy.api.customer.dto.CustomerDTO;
import com.limasantos.pharmacy.api.customer.dto.CustomerDetailDTO;
import com.limasantos.pharmacy.api.customer.dto.CustomerPurchaseHistoryDTO;
import com.limasantos.pharmacy.api.customer.entity.Customer;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CustomerMapper {


    //dto para traduzir a entidade Customer para CustomerDTO

    public CustomerDTO toDTO(Customer customer) {
        if (customer == null) {
            return null;
        }
        
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setCpf(customer.getCpf());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setAddress(customer.getAddress());
        
        return dto;
    }

    // Metodo para converter uma lista de Customer para uma lista de CustomerDTO
    public List<CustomerDTO> toDTOList(List<Customer> customers) {
        return customers.stream()
                .map(this::toDTO)
                .toList();
    }


   //criacao de customer a partir de dto de criacao
    public Customer toEntity(CreateCustomerDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Customer customer = new Customer();
        customer.setName(dto.getName());
        customer.setCpf(dto.getCpf());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());
        
        return customer;
    }


    // Converte Customer para DTO detalhado (com histórico de compras e totais gastos)
    public CustomerDetailDTO toDetailDTO(Customer customer) {
        if (customer == null) {
            return null;
        }
        
        CustomerDetailDTO dto = new CustomerDetailDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setCpf(customer.getCpf());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setAddress(customer.getAddress());
        
        // Mapear histórico de compras
        List<CustomerPurchaseHistoryDTO> purchaseHistory = customer.getPurchaseHistory()
                .stream()
                .map(sale -> new CustomerPurchaseHistoryDTO(
                        sale.getId(),
                        sale.getSaleDate(),
                        sale.getTotalAmount(),
                        sale.getPaymentMethod().name()
                ))
                .toList();
        
        dto.setPurchaseHistory(purchaseHistory);
        
        // Calcular totais
        BigDecimal totalSpent = customer.getPurchaseHistory()
                .stream()
                .map(sale -> sale.getTotalAmount() != null ? sale.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        dto.setTotalSpent(totalSpent);
        dto.setTotalPurchases(customer.getPurchaseHistory().size());
        
        return dto;
    }
}

