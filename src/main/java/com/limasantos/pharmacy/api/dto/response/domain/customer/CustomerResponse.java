package com.limasantos.pharmacy.api.dto.response.domain.customer;

import com.limasantos.pharmacy.api.customer.dto.CustomerDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    private Long id;
    private String name;
    private String cpf;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CustomerResponse fromDto(CustomerDTO dto) {
        return new CustomerResponse(dto.getId(), dto.getName(), dto.getCpf(), null, null);
    }
}

