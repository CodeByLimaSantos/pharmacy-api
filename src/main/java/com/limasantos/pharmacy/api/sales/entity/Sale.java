package com.limasantos.pharmacy.api.sales.entity;

import com.limasantos.pharmacy.api.customer.entity.Customer;
import com.limasantos.pharmacy.api.shared.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TB_sales")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = true)
    private Customer customer;

    @Column(nullable = false, updatable = false)
    private LocalDateTime saleDate;

    @Column(nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleItem> items = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.saleDate = LocalDateTime.now();
    }

    public void addItem(SaleItem item) {
        item.setSale(this);
        this.items.add(item);
        recalcularTotal();
    }

    public void removeItem(SaleItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item não pode ser nulo");
        }

        if (!this.items.contains(item)) {
            throw new IllegalStateException("Item não pertence à venda");
        }

        this.items.remove(item);
        item.setSale(null);
        recalcularTotal();
    }

    private void recalcularTotal() {
        this.totalAmount = items.stream()
                .map(i -> i.getPriceAtSale().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}