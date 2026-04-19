package com.limasantos.pharmacy.api.dto.response.domain.sale;

import com.limasantos.pharmacy.api.sales.dto.SaleDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleListResponse {
    private Long id;
    private String customerName;
    private LocalDateTime saleDate;
    private BigDecimal totalAmount;

    public static SaleListResponse fromDto(SaleDTO dto) {
        return new SaleListResponse(dto.getId(), dto.getCustomerName(), dto.getSaleDate(), dto.getTotalAmount());
    }
}

