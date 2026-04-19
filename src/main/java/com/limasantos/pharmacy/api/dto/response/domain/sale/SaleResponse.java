package com.limasantos.pharmacy.api.dto.response.domain.sale;

import com.limasantos.pharmacy.api.sales.dto.SaleDTO;
import com.limasantos.pharmacy.api.sales.dto.SaleItemDTO;
import com.limasantos.pharmacy.api.shared.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleResponse {
    private Long id;
    private Long customerId;
    private String customerName;
    private LocalDateTime saleDate;
    private BigDecimal totalAmount;
    private PaymentMethod paymentMethod;
    private List<SaleItemDTO> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SaleResponse fromDto(SaleDTO dto) {
        return new SaleResponse(
                dto.getId(),
                dto.getCustomerId(),
                dto.getCustomerName(),
                dto.getSaleDate(),
                dto.getTotalAmount(),
                dto.getPaymentMethod(),
                dto.getItems(),
                null,
                null
        );
    }
}

