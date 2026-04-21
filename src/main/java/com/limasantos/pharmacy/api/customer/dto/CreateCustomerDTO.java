package com.limasantos.pharmacy.api.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateCustomerDTO {

    @NotBlank(message = "Nome e obrigatorio")
    @Size(min = 2, max = 255, message = "Nome deve ter entre 2 e 255 caracteres")
    private String name;

    @NotBlank(message = "CPF e obrigatorio")
    @CPF(message = "CPF invalido")
    private String cpf;

    @Email(message = "Email invalido")
    @Size(max = 255, message = "Email deve ter no maximo 255 caracteres")
    private String email;

    @Size(max = 20, message = "Telefone deve ter no maximo 20 caracteres")
    private String phone;

    @Size(max = 255, message = "Endereco deve ter no maximo 255 caracteres")
    private String address;

}


//dto para criar novo cliente, contendo os campos necessários para a criação de um cliente,
// como nome e CPF. Ele é usado para receber os dados do cliente em endpoints de criação.

