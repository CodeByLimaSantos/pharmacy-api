package com.limasantos.pharmacy.api.dto.response.domain.financial;

import com.limasantos.pharmacy.api.financial.dto.FinancialSummaryDTO;
import com.limasantos.pharmacy.api.financial.entity.Financial.FinancialType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialSummaryResponse {
    private FinancialType type;
    private BigDecimal totalReceivable;
    private BigDecimal totalPayable;
    private BigDecimal totalReceived;
    private BigDecimal totalPaid;
    private BigDecimal pendingReceivable;
    private BigDecimal pendingPayable;
    private Integer countPendingReceivable;
    private Integer countPendingPayable;
    private BigDecimal overdueReceivable;
    private BigDecimal overduePayable;
    private Integer countOverdueReceivable;
    private Integer countOverduePayable;
    private Double receivedRate;
    private Double paidRate;

    public static FinancialSummaryResponse fromDto(FinancialSummaryDTO dto) {
        return new FinancialSummaryResponse(
                dto.getType(),
                dto.getTotalReceivable(),
                dto.getTotalPayable(),
                dto.getTotalReceived(),
                dto.getTotalPaid(),
                dto.getPendingReceivable(),
                dto.getPendingPayable(),
                dto.getCountPendingReceivable(),
                dto.getCountPendingPayable(),
                dto.getOverdueReceivable(),
                dto.getOverduePayable(),
                dto.getCountOverdueReceivable(),
                dto.getCountOverduePayable(),
                dto.getReceivedRate(),
                dto.getPaidRate()
        );
    }
}

