package com.limasantos.pharmacy.api.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomerDTO {
    private Long id;
    private String name;
    private String cpf;
    private String email;
    private String phone;
    private String address;
}

//dto para listagens e respostas básicas, sem detalhes de histórico de compras ou outros campos adicionais.
// Ele é usado para retornar informações essenciais do cliente em endpoints que não exigem detalhes completos.