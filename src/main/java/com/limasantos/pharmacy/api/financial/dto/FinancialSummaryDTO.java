package com.limasantos.pharmacy.api.financial.dto;

import com.limasantos.pharmacy.api.financial.entity.Financial.FinancialType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FinancialSummaryDTO {

    private FinancialType type;

    // Resumo geral
    private BigDecimal totalReceivable; // Total a receber
    private BigDecimal totalPayable; // Total a pagar
    private BigDecimal totalReceived; // Total recebido
    private BigDecimal totalPaid; // Total pago

    // Pendentes
    private BigDecimal pendingReceivable; // Pendente a receber
    private BigDecimal pendingPayable; // Pendente a pagar
    private Integer countPendingReceivable;
    private Integer countPendingPayable;

    // Vencidos
    private BigDecimal overdueReceivable;
    private BigDecimal overduePayable;
    private Integer countOverdueReceivable;
    private Integer countOverduePayable;

    // Taxa de recebimento/pagamento
    private Double receivedRate; // Percentual recebido (0-100)
    private Double paidRate; // Percentual pago (0-100)
}

