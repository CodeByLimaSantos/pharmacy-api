package com.limasantos.pharmacy.api.product.dto;

import com.limasantos.pharmacy.api.category.entity.entity.ProductCategoryTypes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal priceCost;
    private BigDecimal priceSale;
    private Boolean controlled;
    private String tarja;
    private String registerMS;
    private ProductCategoryTypes productCategoryType;
    private Long supplierId;
    private String supplierName;
}

