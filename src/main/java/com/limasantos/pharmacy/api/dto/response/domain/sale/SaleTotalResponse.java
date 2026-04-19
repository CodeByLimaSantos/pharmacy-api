package com.limasantos.pharmacy.api.dto.response.domain.sale;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleTotalResponse {
    private BigDecimal totalAmount;
}

