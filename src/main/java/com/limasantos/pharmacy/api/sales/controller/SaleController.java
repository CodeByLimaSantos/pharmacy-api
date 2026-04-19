package com.limasantos.pharmacy.api.sales.controller;

import com.limasantos.pharmacy.api.dto.response.base.ApiResponse;
import com.limasantos.pharmacy.api.dto.response.base.DeleteResponse;
import com.limasantos.pharmacy.api.dto.response.base.PaginatedResponse;
import com.limasantos.pharmacy.api.dto.response.domain.sale.SaleListResponse;
import com.limasantos.pharmacy.api.dto.response.domain.sale.SaleResponse;
import com.limasantos.pharmacy.api.dto.response.domain.sale.SaleTotalResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.limasantos.pharmacy.api.sales.dto.CreateSaleDTO;
import com.limasantos.pharmacy.api.sales.dto.SaleDTO;
import com.limasantos.pharmacy.api.sales.dto.SaleDetailDTO;
import com.limasantos.pharmacy.api.sales.service.SaleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
@Tag(name = "Vendas", description = "Registro de vendas, consultas por cliente e consolidação de totais")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping
    @Operation(summary = "Criar venda", description = "Registra uma nova venda com itens, cliente e forma de pagamento.")
    public ResponseEntity<ApiResponse<SaleResponse>> create(@Valid @RequestBody CreateSaleDTO dto,
                                                            HttpServletRequest request) {
        SaleDTO created = saleService.createSale(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<SaleResponse>builder()
                        .success(true)
                        .message("Venda criada com sucesso")
                        .code("SALE_CREATED")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(SaleResponse.fromDto(created))
                        .build()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar venda por ID", description = "Retorna os dados resumidos de uma venda.")
    public ResponseEntity<ApiResponse<SaleResponse>> findById(@PathVariable Long id,
                                                              HttpServletRequest request) {
        SaleDTO sale = saleService.findById(id);
        return ResponseEntity.ok(
                ApiResponse.<SaleResponse>builder()
                        .success(true)
                        .message("Venda encontrada")
                        .code("SALE_FOUND")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(SaleResponse.fromDto(sale))
                        .build()
        );
    }

    @GetMapping("/{id}/detail")
    @Operation(summary = "Buscar detalhes da venda", description = "Retorna itens detalhados e metadados da venda.")
    public ResponseEntity<ApiResponse<SaleDetailDTO>> findDetailById(@PathVariable Long id,
                                                                      HttpServletRequest request) {
        SaleDetailDTO detail = saleService.findDetailById(id);
        return ResponseEntity.ok(
                ApiResponse.<SaleDetailDTO>builder()
                        .success(true)
                        .message("Detalhes da venda encontrados")
                        .code("SALE_DETAIL_FOUND")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(detail)
                        .build()
        );
    }

    @GetMapping
    @Operation(summary = "Listar vendas", description = "Retorna todas as vendas em envelope paginado padrão.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de vendas", content = @Content(schema = @Schema(implementation = PaginatedResponse.class)))
    })
    public ResponseEntity<PaginatedResponse<SaleListResponse>> findAll(HttpServletRequest request) {
        List<SaleListResponse> sales = saleService.findAll().stream()
                .map(SaleListResponse::fromDto)
                .toList();
        return ResponseEntity.ok(
                PaginatedResponse.<SaleListResponse>paginatedBuilder()
                        .success(true)
                        .message("Vendas recuperadas com sucesso")
                        .code("SALES_FOUND")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(sales)
                        .page(0)
                        .pageSize(sales.size())
                        .totalElements(sales.size())
                        .totalPages(sales.isEmpty() ? 0 : 1)
                        .hasNext(false)
                        .hasPrevious(false)
                        .build()
        );
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Listar vendas por cliente", description = "Filtra as vendas associadas a um cliente específico.")
    public ResponseEntity<PaginatedResponse<SaleListResponse>> findByCustomer(@PathVariable Long customerId,
                                                                               HttpServletRequest request) {
        List<SaleListResponse> sales = saleService.findByCustomer(customerId).stream()
                .map(SaleListResponse::fromDto)
                .toList();
        return ResponseEntity.ok(
                PaginatedResponse.<SaleListResponse>paginatedBuilder()
                        .success(true)
                        .message("Vendas do cliente recuperadas")
                        .code("CUSTOMER_SALES_FOUND")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(sales)
                        .page(0)
                        .pageSize(sales.size())
                        .totalElements(sales.size())
                        .totalPages(sales.isEmpty() ? 0 : 1)
                        .hasNext(false)
                        .hasPrevious(false)
                        .build()
        );
    }

    @GetMapping("/total/amount")
    @Operation(summary = "Obter total geral de vendas", description = "Calcula e retorna o total financeiro de todas as vendas.")
    public ResponseEntity<ApiResponse<SaleTotalResponse>> getTotalSalesAmount(HttpServletRequest request) {
        BigDecimal total = saleService.getTotalSalesAmount();
        return ResponseEntity.ok(
                ApiResponse.<SaleTotalResponse>builder()
                        .success(true)
                        .message("Total de vendas calculado")
                        .code("SALES_TOTAL_FOUND")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(new SaleTotalResponse(total))
                        .build()
        );
    }

    @GetMapping("/customer/{customerId}/total")
    @Operation(summary = "Obter total de vendas por cliente", description = "Calcula o valor total vendido para um cliente específico.")
    public ResponseEntity<ApiResponse<SaleTotalResponse>> getCustomerTotalSales(@PathVariable Long customerId,
                                                                                 HttpServletRequest request) {
        BigDecimal total = saleService.getCustomerTotalSales(customerId);
        return ResponseEntity.ok(
                ApiResponse.<SaleTotalResponse>builder()
                        .success(true)
                        .message("Total de vendas do cliente calculado")
                        .code("CUSTOMER_SALES_TOTAL_FOUND")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(new SaleTotalResponse(total))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar venda", description = "Cancela uma venda pelo ID e retorna confirmação da operação.")
    public ResponseEntity<ApiResponse<DeleteResponse>> cancelSale(@PathVariable Long id,
                                                                   HttpServletRequest request) {
        saleService.cancelSale(id);
        return ResponseEntity.ok(
                ApiResponse.<DeleteResponse>builder()
                        .success(true)
                        .message("Venda cancelada com sucesso")
                        .code("SALE_CANCELED")
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
