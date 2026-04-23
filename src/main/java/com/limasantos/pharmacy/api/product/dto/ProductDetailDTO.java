package com.limasantos.pharmacy.api.product.dto;

import com.limasantos.pharmacy.api.category.entity.ProductCategoryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductDetailDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal priceCost;
    private BigDecimal priceSale;
    private BigDecimal margin; // Margem de lucro calculada
    private Boolean controlled;
    private String tarja;
    private String registerMS;
    private ProductCategoryType productCategoryType;
    private Long supplierId;
    private String supplierName;
    private Integer currentStock; // Estoque atual
    private List<ProductMovementDTO> stockMovements; // Movimentações de estoque
}

