package com.limasantos.pharmacy.api.dto.response.domain.financial;

import com.limasantos.pharmacy.api.financial.dto.FinancialDTO;
import com.limasantos.pharmacy.api.financial.entity.Financial.FinancialType;
import com.limasantos.pharmacy.api.financial.entity.Financial.PaymentStatus;
import com.limasantos.pharmacy.api.shared.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialResponse {
    private Long id;
    private FinancialType type;
    private String description;
    private BigDecimal amount;
    private LocalDateTime issueDate;
    private LocalDate dueDate;
    private LocalDate paymentDate;
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
    private Long customerId;
    private String customerName;
    private Long supplierId;
    private String supplierName;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static FinancialResponse fromDto(FinancialDTO dto) {
        return new FinancialResponse(
                dto.getId(),
                dto.getType(),
                dto.getDescription(),
                dto.getAmount(),
                dto.getIssueDate(),
                dto.getDueDate(),
                dto.getPaymentDate(),
                dto.getStatus(),
                dto.getPaymentMethod(),
                dto.getCustomerId(),
                dto.getCustomerName(),
                dto.getSupplierId(),
                dto.getSupplierName(),
                dto.getNotes(),
                null,
                null
        );
    }
}

