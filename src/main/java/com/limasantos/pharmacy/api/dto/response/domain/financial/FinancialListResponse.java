package com.limasantos.pharmacy.api.dto.response.domain.financial;

import com.limasantos.pharmacy.api.financial.dto.FinancialDTO;
import com.limasantos.pharmacy.api.financial.entity.Financial.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialListResponse {


    private Long id;
    private String description;
    private BigDecimal amount;
    private LocalDate dueDate;
    private PaymentStatus status;

    public static FinancialListResponse fromDto(FinancialDTO dto) {
        return new FinancialListResponse(
                dto.getId(),
                dto.getDescription(),
                dto.getAmount(),
                dto.getDueDate(),
                dto.getStatus()
        );
    }
}

