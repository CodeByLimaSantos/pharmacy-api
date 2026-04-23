package com.limasantos.pharmacy.api.product.dto;

import com.limasantos.pharmacy.api.category.entity.ProductCategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateProductDTO {
    
    @NotBlank(message = "O nome do produto é obrigatório")
    private String name;

    @NotBlank(message = "A descrição do produto é obrigatória")
    private String description;

    @NotNull(message = "O preço de custo é obrigatório")
    @PositiveOrZero(message = "O preço de custo não pode ser negativo")
    private BigDecimal priceCost;

    @NotNull(message = "O preço de venda é obrigatório")
    @PositiveOrZero(message = "O preço de venda não pode ser negativo")
    private BigDecimal priceSale;

    @NotNull(message = "É necessário indicar se o produto é controlado")
    private Boolean controlled;

    @NotBlank(message = "A tarja do produto é obrigatória")
    private String tarja;

    @NotBlank(message = "O registro MS é obrigatório")
    @Pattern(regexp = "\\d{13}", message = "Registro MS deve conter exatamente 13 dígitos numéricos")
    private String registerMS;

    @NotNull(message = "A categoria do produto é obrigatória")
    private ProductCategoryType productCategoryType;

    @NotNull(message = "O fornecedor é obrigatório")
    private Long supplierId;
}

