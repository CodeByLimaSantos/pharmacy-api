package com.limasantos.pharmacy.api.supplier.controller;

import com.limasantos.pharmacy.api.dto.response.base.ApiResponse;
import com.limasantos.pharmacy.api.dto.response.base.DeleteResponse;
import com.limasantos.pharmacy.api.dto.response.base.PaginatedResponse;
import com.limasantos.pharmacy.api.dto.response.domain.supplier.SupplierListResponse;
import com.limasantos.pharmacy.api.dto.response.domain.supplier.SupplierResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.limasantos.pharmacy.api.supplier.dto.CreateSupplierDTO;
import com.limasantos.pharmacy.api.supplier.dto.SupplierDTO;
import com.limasantos.pharmacy.api.supplier.dto.SupplierDetailDTO;
import com.limasantos.pharmacy.api.supplier.service.SupplierService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@Tag(name = "Fornecedores", description = "Cadastro de fornecedores e consultas por CNPJ")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @PostMapping
    @Operation(summary = "Criar fornecedor", description = "Cadastra um novo fornecedor no sistema.")
    public ResponseEntity<ApiResponse<SupplierResponse>> create(@Valid @RequestBody CreateSupplierDTO dto,
                                                                HttpServletRequest request) {
        SupplierDTO created = supplierService.createSupplier(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<SupplierResponse>builder()
                        .success(true)
                        .message("Fornecedor criado com sucesso")
                        .code("SUPPLIER_CREATED")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(SupplierResponse.fromDto(created))
                        .build()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar fornecedor por ID", description = "Retorna dados resumidos de um fornecedor.")
    public ResponseEntity<ApiResponse<SupplierResponse>> findById(@PathVariable Long id,
                                                                  HttpServletRequest request) {
        SupplierDTO supplier = supplierService.findById(id);
        return ResponseEntity.ok(
                ApiResponse.<SupplierResponse>builder()
                        .success(true)
                        .message("Fornecedor encontrado")
                        .code("SUPPLIER_FOUND")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(SupplierResponse.fromDto(supplier))
                        .build()
        );
    }

    @GetMapping("/{id}/detail")
    @Operation(summary = "Buscar detalhes do fornecedor", description = "Retorna informações detalhadas e produtos associados.")
    public ResponseEntity<ApiResponse<SupplierDetailDTO>> findDetailById(@PathVariable Long id,
                                                                          HttpServletRequest request) {
        SupplierDetailDTO detail = supplierService.findDetailById(id);
        return ResponseEntity.ok(
                ApiResponse.<SupplierDetailDTO>builder()
                        .success(true)
                        .message("Detalhes do fornecedor encontrados")
                        .code("SUPPLIER_DETAIL_FOUND")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(detail)
                        .build()
        );
    }

    @GetMapping
    @Operation(summary = "Listar fornecedores", description = "Retorna todos os fornecedores no envelope paginado padrão.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de fornecedores", content = @Content(schema = @Schema(implementation = PaginatedResponse.class)))
    })
    public ResponseEntity<PaginatedResponse<SupplierListResponse>> findAll(HttpServletRequest request) {
        List<SupplierListResponse> suppliers = supplierService.findAll().stream()
                .map(SupplierListResponse::fromDto)
                .toList();
        return ResponseEntity.ok(
                PaginatedResponse.<SupplierListResponse>paginatedBuilder()
                        .success(true)
                        .message("Fornecedores recuperados com sucesso")
                        .code("SUPPLIERS_FOUND")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(suppliers)
                        .page(0)
                        .pageSize(suppliers.size())
                        .totalElements(suppliers.size())
                        .totalPages(suppliers.isEmpty() ? 0 : 1)
                        .hasNext(false)
                        .hasPrevious(false)
                        .build()
        );
    }

    @GetMapping("/cnpj/{cnpj}")
    @Operation(summary = "Buscar fornecedor por CNPJ", description = "Localiza um fornecedor pelo CNPJ informado.")
    public ResponseEntity<ApiResponse<SupplierResponse>> findByCnpj(@PathVariable String cnpj,
                                                                     HttpServletRequest request) {
        SupplierDTO supplier = supplierService.findByCnpj(cnpj);
        return ResponseEntity.ok(
                ApiResponse.<SupplierResponse>builder()
                        .success(true)
                        .message("Fornecedor encontrado por CNPJ")
                        .code("SUPPLIER_FOUND_BY_CNPJ")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(SupplierResponse.fromDto(supplier))
                        .build()
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar fornecedor", description = "Atualiza os dados de um fornecedor existente.")
    public ResponseEntity<ApiResponse<SupplierResponse>> update(@PathVariable Long id,
                                                                @Valid @RequestBody CreateSupplierDTO dto,
                                                                HttpServletRequest request) {
        SupplierDTO updated = supplierService.update(id, dto);
        return ResponseEntity.ok(
                ApiResponse.<SupplierResponse>builder()
                        .success(true)
                        .message("Fornecedor atualizado com sucesso")
                        .code("SUPPLIER_UPDATED")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(SupplierResponse.fromDto(updated))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir fornecedor", description = "Exclui um fornecedor e retorna confirmação da operação.")
    public ResponseEntity<ApiResponse<DeleteResponse>> delete(@PathVariable Long id,
                                                               HttpServletRequest request) {
        supplierService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.<DeleteResponse>builder()
                        .success(true)
                        .message("Fornecedor deletado com sucesso")
                        .code("SUPPLIER_DELETED")
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
