package com.limasantos.pharmacy.api.dto.response.domain.supplier;

import com.limasantos.pharmacy.api.supplier.dto.SupplierDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierResponse {
    private Long id;
    private String name;
    private String cnpj;
    private String email;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SupplierResponse fromDto(SupplierDTO dto) {
        return new SupplierResponse(
                dto.getId(),
                dto.getName(),
                dto.getCnpj(),
                dto.getEmail(),
                dto.getPhone(),
                null,
                null
        );
    }
}

