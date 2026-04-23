package com.limasantos.pharmacy.api.dto.response.domain.product;

import com.limasantos.pharmacy.api.category.entity.ProductCategoryType;
import com.limasantos.pharmacy.api.product.dto.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal priceCost;
    private BigDecimal priceSale;
    private Boolean controlled;
    private String tarja;
    private String registerMS;
    private ProductCategoryType productCategoryType;
    private Long supplierId;
    private String supplierName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProductResponse fromDto(ProductDTO dto) {
        return new ProductResponse(
                dto.getId(),
                dto.getName(),
                dto.getDescription(),
                dto.getPriceCost(),
                dto.getPriceSale(),
                dto.getControlled(),
                dto.getTarja(),
                dto.getRegisterMS(),
                dto.getProductCategoryType(),
                dto.getSupplierId(),
                dto.getSupplierName(),
                null,
                null
        );
    }
}

