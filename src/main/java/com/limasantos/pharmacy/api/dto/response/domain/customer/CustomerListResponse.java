package com.limasantos.pharmacy.api.dto.response.domain.customer;

import com.limasantos.pharmacy.api.customer.dto.CustomerDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerListResponse {
    private Long id;
    private String name;
    private String cpf;
    private String email;
    private String phone;
    private String address;

    public static CustomerListResponse fromDto(CustomerDTO dto) {
        return new CustomerListResponse(
                dto.getId(),
                dto.getName(),
                dto.getCpf(),
                dto.getEmail(),
                dto.getPhone(),
                dto.getAddress()
        );
    }
}

