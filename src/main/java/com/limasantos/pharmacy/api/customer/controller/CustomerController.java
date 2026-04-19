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
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/customers")
@Tag(name = "Clientes", description = "Gerenciamento de clientes e consulta por CPF")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @Operation(summary = "Criar cliente", description = "Cadastra um novo cliente no sistema.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Cliente criado", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<ApiResponse<CustomerResponse>> create(@Valid @RequestBody CreateCustomerDTO dto,
                                                                HttpServletRequest request) {
        CustomerDTO created = customerService.createCustomer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<CustomerResponse>builder()
                        .success(true)
                        .message("Cliente criado com sucesso")
                        .code("CUSTOMER_CREATED")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(CustomerResponse.fromDto(created))
                        .build()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID", description = "Retorna os dados resumidos de um cliente pelo identificador.")
    public ResponseEntity<ApiResponse<CustomerResponse>> findById(@PathVariable Long id,
                                                                  HttpServletRequest request) {
        CustomerDTO customer = customerService.findById(id);
        return ResponseEntity.ok(
                ApiResponse.<CustomerResponse>builder()
                        .success(true)
                        .message("Cliente encontrado")
                        .code("CUSTOMER_FOUND")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(CustomerResponse.fromDto(customer))
                        .build()
        );
    }

    @GetMapping("/{id}/detail")
    @Operation(summary = "Buscar detalhes do cliente", description = "Retorna informações detalhadas incluindo histórico de compras.")
    public ResponseEntity<ApiResponse<CustomerDetailDTO>> findDetailById(@PathVariable Long id,
                                                                          HttpServletRequest request) {
        CustomerDetailDTO detail = customerService.findDetailById(id);
        return ResponseEntity.ok(
                ApiResponse.<CustomerDetailDTO>builder()
                        .success(true)
                        .message("Detalhes do cliente encontrados")
                        .code("CUSTOMER_DETAIL_FOUND")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(detail)
                        .build()
        );
    }

    @GetMapping
    @Operation(summary = "Listar clientes", description = "Retorna todos os clientes no envelope paginado padrão da API.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de clientes", content = @Content(schema = @Schema(implementation = PaginatedResponse.class)))
    })
    public ResponseEntity<PaginatedResponse<CustomerListResponse>> findAll(HttpServletRequest request) {
        List<CustomerListResponse> customers = customerService.findAll().stream()
                .map(CustomerListResponse::fromDto)
                .toList();
        return ResponseEntity.ok(
                PaginatedResponse.<CustomerListResponse>paginatedBuilder()
                        .success(true)
                        .message("Clientes recuperados com sucesso")
                        .code("CUSTOMERS_FOUND")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(customers)
                        .page(0)
                        .pageSize(customers.size())
                        .totalElements(customers.size())
                        .totalPages(customers.isEmpty() ? 0 : 1)
                        .hasNext(false)
                        .hasPrevious(false)
                        .build()
        );
    }

    @GetMapping("/cpf/{cpf}")
    @Operation(summary = "Buscar cliente por CPF", description = "Localiza um cliente pelo CPF informado.")
    public ResponseEntity<ApiResponse<CustomerResponse>> findByCpf(@PathVariable String cpf,
                                                                    HttpServletRequest request) {
        CustomerDTO customer = customerService.findByCpf(cpf);
        return ResponseEntity.ok(
                ApiResponse.<CustomerResponse>builder()
                        .success(true)
                        .message("Cliente encontrado por CPF")
                        .code("CUSTOMER_FOUND_BY_CPF")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(CustomerResponse.fromDto(customer))
                        .build()
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cliente", description = "Atualiza os dados de um cliente existente.")
    public ResponseEntity<ApiResponse<CustomerResponse>> update(@PathVariable Long id,
                                                                @Valid @RequestBody CreateCustomerDTO dto,
                                                                HttpServletRequest request) {
        CustomerDTO updated = customerService.update(id, dto);
        return ResponseEntity.ok(
                ApiResponse.<CustomerResponse>builder()
                        .success(true)
                        .message("Cliente atualizado com sucesso")
                        .code("CUSTOMER_UPDATED")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(CustomerResponse.fromDto(updated))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir cliente", description = "Exclui um cliente pelo ID e retorna confirmação da operação.")
    public ResponseEntity<ApiResponse<DeleteResponse>> delete(@PathVariable Long id,
                                                               HttpServletRequest request) {
        customerService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.<DeleteResponse>builder()
                        .success(true)
                        .message("Cliente deletado com sucesso")
                        .code("CUSTOMER_DELETED")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(new DeleteResponse(id, true))
                        .build()
        );
    }

    private String resolvePath(HttpServletRequest request) {
        String queryString = request.getQueryString();
        return queryString == null ? request.getRequestURI() : request.getRequestURI() + "?" + queryString;
    }
}
