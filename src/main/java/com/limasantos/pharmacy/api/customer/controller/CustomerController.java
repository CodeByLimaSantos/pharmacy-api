package com.limasantos.pharmacy.api.customer.controller;

import com.limasantos.pharmacy.api.customer.dto.CreateCustomerDTO;
import com.limasantos.pharmacy.api.customer.dto.CustomerDTO;
import com.limasantos.pharmacy.api.customer.dto.CustomerDetailDTO;
import com.limasantos.pharmacy.api.customer.service.CustomerService;
import com.limasantos.pharmacy.api.dto.response.base.ApiResponse;
import com.limasantos.pharmacy.api.dto.response.base.DeleteResponse;
import com.limasantos.pharmacy.api.dto.response.base.PaginatedResponse;
import com.limasantos.pharmacy.api.dto.response.domain.customer.CustomerListResponse;
import com.limasantos.pharmacy.api.dto.response.domain.customer.CustomerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/customer")
@Tag(name = "Clientes", description = "Gerenciamento de clientes e consulta por CPF")
public class CustomerController {

    private final CustomerService customerService;



    public CustomerController(CustomerService customerService) {

        this.customerService = customerService;

    }





    // create customer
    @PostMapping("/create")
    @Operation(summary = "Criar cliente", description = "Cadastra um novo cliente no sistema.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Cliente criado", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<ApiResponse<CustomerResponse>> create(
            @Valid @RequestBody CreateCustomerDTO dto,
            HttpServletRequest request
    ) {

        CustomerDTO created = customerService.createCustomer(dto);

        return createdResponse(
                "Cliente criado com sucesso",
                "CUSTOMER_CREATED",
                CustomerResponse.fromDto(created),
                request
        );

    }



    // search all customers
    @GetMapping("/search")
    @Operation(summary = "Listar clientes", description = "Retorna todos os clientes no envelope paginado padrão da API.")
    public ResponseEntity<PaginatedResponse<CustomerListResponse>> findAll(
            HttpServletRequest request
    ) {

        List<CustomerListResponse> customers = customerService.findAll()
                .stream()
                .map(CustomerListResponse::fromDto)
                .toList();

        return okPaginatedResponse(
                "Clientes recuperados com sucesso",
                "CUSTOMERS_FOUND",
                customers,
                request
        );

    }



    // search customer by id
    @GetMapping("/search/{id}")
    @Operation(summary = "Buscar cliente por ID", description = "Retorna os dados resumidos de um cliente pelo identificador.")
    public ResponseEntity<ApiResponse<CustomerResponse>> findById(
            @PathVariable Long id,
            HttpServletRequest request
    ) {

        CustomerDTO customer = customerService.findById(id);

        return okResponse(
                "Cliente encontrado",
                "CUSTOMER_FOUND",
                CustomerResponse.fromDto(customer),
                request
        );

    }



    // search customer details
    @GetMapping("/searchDetails/{id}")
    @Operation(summary = "Buscar detalhes do cliente", description = "Retorna informações detalhadas incluindo histórico de compras.")
    public ResponseEntity<ApiResponse<CustomerDetailDTO>> findDetailById(
            @PathVariable Long id,
            HttpServletRequest request
    ) {

        CustomerDetailDTO detail = customerService.findDetailById(id);

        return okResponse(
                "Detalhes do cliente encontrados",
                "CUSTOMER_DETAIL_FOUND",
                detail,
                request
        );

    }



    // search customer by cpf
    @GetMapping("/cpf/{cpf}")
    @Operation(summary = "Buscar cliente por CPF", description = "Localiza um cliente pelo CPF informado.")
    public ResponseEntity<ApiResponse<CustomerResponse>> findByCpf(
            @PathVariable String cpf,
            HttpServletRequest request
    ) {

        CustomerDTO customer = customerService.findByCpf(cpf);

        return okResponse(
                "Cliente encontrado por CPF",
                "CUSTOMER_FOUND_BY_CPF",
                CustomerResponse.fromDto(customer),
                request
        );

    }



    // update customer
    @PutMapping("/update/{id}")
    @Operation(summary = "Atualizar cliente", description = "Atualiza os dados de um cliente existente.")
    public ResponseEntity<ApiResponse<CustomerResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody CreateCustomerDTO dto,
            HttpServletRequest request
    ) {

        CustomerDTO updated = customerService.update(id, dto);

        return okResponse(
                "Cliente atualizado com sucesso",
                "CUSTOMER_UPDATED",
                CustomerResponse.fromDto(updated),
                request
        );

    }



    // delete customer
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Excluir cliente", description = "Exclui um cliente pelo ID e retorna confirmação da operação.")
    public ResponseEntity<ApiResponse<DeleteResponse>> delete(
            @PathVariable Long id,
            HttpServletRequest request
    ) {

        customerService.delete(id);

        return okResponse(
                "Cliente deletado com sucesso",
                "CUSTOMER_DELETED",
                new DeleteResponse(id, true),
                request
        );

    }










    // helper methods
    private <T> ResponseEntity<ApiResponse<T>> createdResponse(
            String message,
            String code,
            T data,
            HttpServletRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(buildApiResponse(message, code, data, request));

    }



    private <T> ResponseEntity<ApiResponse<T>> okResponse(
            String message,
            String code,
            T data,
            HttpServletRequest request
    ) {

        return ResponseEntity
                .ok(buildApiResponse(message, code, data, request));

    }



    private <T> ApiResponse<T> buildApiResponse(
            String message,
            String code,
            T data,
            HttpServletRequest request
    ) {

        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .code(code)
                .timestamp(LocalDateTime.now())
                .path(resolvePath(request))
                .data(data)
                .build();

    }



    private <T> ResponseEntity<PaginatedResponse<T>> okPaginatedResponse(
            String message,
            String code,
            List<T> data,
            HttpServletRequest request
    ) {

        return ResponseEntity.ok(
                PaginatedResponse.<T>paginatedBuilder()
                        .success(true)
                        .message(message)
                        .code(code)
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(data)
                        .page(0)
                        .pageSize(data.size())
                        .totalElements(data.size())
                        .totalPages(data.isEmpty() ? 0 : 1)
                        .hasNext(false)
                        .hasPrevious(false)
                        .build()
        );

    }



    private String resolvePath(HttpServletRequest request) {

        String queryString = request.getQueryString();

        return (queryString == null)
                ? request.getRequestURI()
                : request.getRequestURI() + "?" + queryString;

    }

}