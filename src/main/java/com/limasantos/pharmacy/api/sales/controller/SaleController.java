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
@RequestMapping("/sales")
@Tag(name = "Vendas", description = "Registro de vendas, consultas por cliente e consolidação de totais")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }


    //create sale
    @PostMapping("/create")
    @Operation(summary = "Criar venda", description = "Registra uma nova venda com itens, cliente e forma de pagamento.")
    public ResponseEntity<ApiResponse<SaleResponse>> create(
            @Valid @RequestBody CreateSaleDTO dto,
            HttpServletRequest request) {
        SaleDTO created = saleService.createSale(dto);

        return createdResponse(
                "Venda criada com sucesso",
                "SALE_CREATED",
                SaleResponse.fromDto(created),
                request

        );


    }


    //search sale by id
    @GetMapping("/{id}")
    @Operation(summary = "Buscar venda por ID", description = "Retorna os dados resumidos de uma venda.")
    public ResponseEntity<ApiResponse<SaleResponse>> findById(
            @PathVariable Long id,
            HttpServletRequest request) {

        SaleDTO sale = saleService.findById(id);

        return okResponse(
                "Venda encontrada",
                "SALE_FOUND",
                SaleResponse.fromDto(sale),
                request

        );


    }


    //search sale details by id
    @GetMapping("/searchDetails/{id}")
    @Operation(summary = "Buscar detalhes da venda", description = "Retorna itens detalhados e metadados da venda.")
    public ResponseEntity<ApiResponse<SaleDetailDTO>> findDetailById(
            @PathVariable Long id,
            HttpServletRequest request) {

        SaleDetailDTO detail = saleService.findDetailById(id);

        return okResponse(
                "Detalhes da venda encontrados",
                "SALE_DETAIL_FOUND",
                detail,
                request

        );
    }


    //list all sales
    @GetMapping("/all")
    @Operation(summary = "Listar vendas", description = "Retorna todas as vendas em envelope paginado padrão.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Lista de vendas",
                    content = @Content(schema = @Schema(implementation = PaginatedResponse.class)))
    })
    public ResponseEntity<PaginatedResponse<SaleListResponse>> findAll(HttpServletRequest request) {
        List<SaleListResponse> sales = saleService.findAll().stream()
                .map(SaleListResponse::fromDto)
                .toList();


        return okPaginatedResponse("Vendas recuperadas com sucesso", "SALES_FOUND", sales, request);


    }


    //list sales by customer
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Listar vendas por cliente", description = "Filtra as vendas associadas a um cliente específico.")
    public ResponseEntity<PaginatedResponse<SaleListResponse>> findByCustomer(
            @PathVariable Long customerId,
            HttpServletRequest request) {
        List<SaleListResponse> sales = saleService.findByCustomer(customerId).stream()
                .map(SaleListResponse::fromDto)
                .toList();

        return okPaginatedResponse("Vendas do cliente recuperadas", "CUSTOMER_SALES_FOUND", sales, request);


    }


    //get total sales amount
    @GetMapping("/total/amount")
    @Operation(summary = "Obter total geral de vendas", description = "Calcula e retorna o total financeiro de todas as vendas.")
    public ResponseEntity<ApiResponse<SaleTotalResponse>> getTotalSalesAmount(HttpServletRequest request) {

        BigDecimal total = saleService.getTotalSalesAmount();
        return okResponse(

                "Total de vendas calculado",
                "SALES_TOTAL_FOUND",
                new SaleTotalResponse(total),
                request

        );


    }


    //get total sales amount by customer
    @GetMapping("/customer/{customerId}/total")
    @Operation(summary = "Obter total de vendas por cliente", description = "Calcula o valor total vendido para um cliente específico.")
    public ResponseEntity<ApiResponse<SaleTotalResponse>> getCustomerTotalSales(
            @PathVariable Long customerId,
            HttpServletRequest request) {

        BigDecimal total = saleService.getCustomerTotalSales(customerId);

        return okResponse(
                "Total de vendas do cliente calculado",
                "CUSTOMER_SALES_TOTAL_FOUND",
                new SaleTotalResponse(total),
                request

        );


    }


    //cancel sale
    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar venda", description = "Cancela uma venda pelo ID e retorna confirmação da operação.")
    public ResponseEntity<ApiResponse<DeleteResponse>> cancelSale(
            @PathVariable Long id,
            HttpServletRequest request) {

        saleService.cancelSale(id);

        return okResponse(

                "Venda cancelada com sucesso",
                "SALE_CANCELED",
                new DeleteResponse(id, true),
                request

        );

    }





    /// methods to build responses
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