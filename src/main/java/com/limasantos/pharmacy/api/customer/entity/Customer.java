package com.limasantos.pharmacy.api.customer.entity;


import com.limasantos.pharmacy.api.sales.entity.Sale;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TB_customers")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class Customer {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @CPF
    @Column(nullable = false, unique = true)
    private String cpf;

    private String email;

    private String phone;

    private String address;

    // RELACIONAMENTO: Um cliente tem muitas vendas
    // mappedBy = "customer" indica que a entidade 'Sale' é a dona do relacionamento
    // cascade = CascadeType.ALL: operações no cliente (salvar, deletar) afetam suas vendas
    // orphanRemoval = true: se uma venda for removida desta lista, ela é deletada do banco
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Sale> purchaseHistory = new ArrayList<>();

}
