package com.limasantos.pharmacy.api.product.entity;

import com.limasantos.pharmacy.api.category.entity.ProductCategoryType;
import jakarta.persistence.*;
import com.limasantos.pharmacy.api.supplier.entity.Supplier;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

@Entity
@Table(name = "TB_products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "O nome do produto é obrigatório.")
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "A descrição do produto é obrigatória.")
    private String description;

    @Column(nullable = false)
    @PositiveOrZero
    private BigDecimal priceCost;

    @Column(nullable = false)
    @PositiveOrZero
    private BigDecimal priceSale;

    @Column(nullable = false)
    @NotNull(message = "É necessário indicar se o produto é controlado.")
    private Boolean controlled;

    @Column(nullable = false, length = 13)
    @NotBlank
    @Pattern(regexp = "\\d{13}", message = "Registro MS deve conter exatamente 13 dígitos numéricos")
    private String registerMS;


    @Column(nullable = false)
    @NotBlank
    private String tarja;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "A categoria do produto é obrigatória.")
    private ProductCategoryType productCategoryType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    @NotNull
    private Supplier supplier;

}