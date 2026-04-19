package com.limasantos.pharmacy.api.dto.response.domain.supplier;

import com.limasantos.pharmacy.api.supplier.dto.SupplierDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierListResponse {
    private Long id;
    private String name;
    private String cnpj;

    public static SupplierListResponse fromDto(SupplierDTO dto) {
        return new SupplierListResponse(dto.getId(), dto.getName(), dto.getCnpj());
    }
}

