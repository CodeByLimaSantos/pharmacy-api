package com.limasantos.pharmacy.api.financial.controller;

import com.limasantos.pharmacy.api.dto.response.base.ApiResponse;
import com.limasantos.pharmacy.api.dto.response.base.DeleteResponse;
import com.limasantos.pharmacy.api.dto.response.base.PaginatedResponse;
import com.limasantos.pharmacy.api.dto.response.domain.financial.FinancialListResponse;
import com.limasantos.pharmacy.api.dto.response.domain.financial.FinancialResponse;
import com.limasantos.pharmacy.api.dto.response.domain.financial.FinancialSummaryResponse;
import com.limasantos.pharmacy.api.financial.dto.CreateFinancialDTO;
import com.limasantos.pharmacy.api.financial.dto.FinancialDTO;
import com.limasantos.pharmacy.api.financial.dto.FinancialDetailDTO;
import com.limasantos.pharmacy.api.financial.dto.FinancialSummaryDTO;
import com.limasantos.pharmacy.api.financial.service.FinancialService;
import com.limasantos.pharmacy.api.shared.enums.PaymentMethod;
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
@RequestMapping("/financial")
@Tag(name = "Financeiro", description = "Operações de contas a pagar, contas a receber e consolidação financeira")
public class FinancialController {

    private final FinancialService financialService;



    public FinancialController(FinancialService financialService) {

        this.financialService = financialService;

    }



    // create financial
    @PostMapping("/create")
    @Operation(summary = "Criar lançamento financeiro", description = "Cria um novo lançamento financeiro com base nos dados informados.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Lançamento criado", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Erro interno")
    })
    public ResponseEntity<ApiResponse<FinancialResponse>> create(
            @Valid @RequestBody CreateFinancialDTO dto,
            HttpServletRequest request
    ) {

        FinancialDTO created = financialService.createFinancial(dto);

        return createdResponse(
                "Lançamento financeiro criado com sucesso",
                "FINANCIAL_CREATED",
                FinancialResponse.fromDto(created),
                request
        );

    }



    // search by id
    @GetMapping("/search/{id}")
    @Operation(summary = "Buscar lançamento por ID", description = "Retorna um lançamento financeiro resumido pelo identificador.")
    public ResponseEntity<ApiResponse<FinancialResponse>> findById(
            @PathVariable Long id,
            HttpServletRequest request
    ) {

        FinancialDTO financial = financialService.findById(id);

        return okResponse(
                "Lançamento financeiro encontrado",
                "FINANCIAL_FOUND",
                FinancialResponse.fromDto(financial),
                request
        );

    }



    // search detail
    @GetMapping("/detail/{id}")
    @Operation(summary = "Buscar detalhes do lançamento", description = "Retorna os dados detalhados de um lançamento financeiro por ID.")
    public ResponseEntity<ApiResponse<FinancialDetailDTO>> findDetailById(
            @PathVariable Long id,
            HttpServletRequest request
    ) {

        FinancialDetailDTO detail = financialService.findDetailById(id);

        return okResponse(
                "Detalhes do lançamento financeiro encontrados",
                "FINANCIAL_DETAIL_FOUND",
                detail,
                request
        );

    }



    // list all
    @GetMapping
    @Operation(summary = "Listar lançamentos financeiros", description = "Retorna todos os lançamentos financeiros no envelope paginado da API.")
    public ResponseEntity<PaginatedResponse<FinancialListResponse>> findAll(
            HttpServletRequest request
    ) {

        List<FinancialListResponse> financials = financialService.findAll()
                .stream()
                .map(FinancialListResponse::fromDto)
                .toList();

        return okPaginatedResponse(
                "Lançamentos financeiros recuperados",
                "FINANCIALS_FOUND",
                financials,
                request
        );

    }



    // pending
    @GetMapping("/pending")
    public ResponseEntity<PaginatedResponse<FinancialListResponse>> findPending(
            HttpServletRequest request
    ) {

        List<FinancialListResponse> pending = financialService.findPending()
                .stream()
                .map(FinancialListResponse::fromDto)
                .toList();

        return okPaginatedResponse(
                "Pendências financeiras recuperadas",
                "FINANCIAL_PENDING_FOUND",
                pending,
                request
        );

    }



    // overdue
    @GetMapping("/overdue")
    public ResponseEntity<PaginatedResponse<FinancialListResponse>> findOverdue(
            HttpServletRequest request
    ) {

        List<FinancialListResponse> overdue = financialService.findOverdue()
                .stream()
                .map(FinancialListResponse::fromDto)
                .toList();

        return okPaginatedResponse(
                "Lançamentos vencidos recuperados",
                "FINANCIAL_OVERDUE_FOUND",
                overdue,
                request
        );

    }



    // receivable
    @GetMapping("/receivable")
    public ResponseEntity<PaginatedResponse<FinancialListResponse>> findReceivable(
            HttpServletRequest request
    ) {

        List<FinancialListResponse> receivable = financialService.findReceivable()
                .stream()
                .map(FinancialListResponse::fromDto)
                .toList();

        return okPaginatedResponse(
                "Contas a receber recuperadas",
                "FINANCIAL_RECEIVABLE_FOUND",
                receivable,
                request
        );

    }



    // payable
    @GetMapping("/payable")
    public ResponseEntity<PaginatedResponse<FinancialListResponse>> findPayable(
            HttpServletRequest request
    ) {

        List<FinancialListResponse> payable = financialService.findPayable()
                .stream()
                .map(FinancialListResponse::fromDto)
                .toList();

        return okPaginatedResponse(
                "Contas a pagar recuperadas",
                "FINANCIAL_PAYABLE_FOUND",
                payable,
                request
        );

    }



    // update
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<FinancialResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody CreateFinancialDTO dto,
            HttpServletRequest request
    ) {

        FinancialDTO updated = financialService.update(id, dto);

        return okResponse(
                "Lançamento financeiro atualizado",
                "FINANCIAL_UPDATED",
                FinancialResponse.fromDto(updated),
                request
        );

    }



    // mark as paid
    @PostMapping("/{id}/pay")
    public ResponseEntity<ApiResponse<FinancialResponse>> markAsPaid(
            @PathVariable Long id,
            @RequestParam PaymentMethod paymentMethod,
            HttpServletRequest request
    ) {

        FinancialDTO paid = financialService.markAsPaid(id, paymentMethod);

        return okResponse(
                "Lançamento marcado como pago",
                "FINANCIAL_MARKED_PAID",
                FinancialResponse.fromDto(paid),
                request
        );

    }



    // delete
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<DeleteResponse>> delete(
            @PathVariable Long id,
            HttpServletRequest request
    ) {

        financialService.delete(id);

        return okResponse(
                "Lançamento financeiro removido",
                "FINANCIAL_DELETED",
                new DeleteResponse(id, true),
                request
        );

    }



    // summary
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<FinancialSummaryResponse>> generateSummary(
            HttpServletRequest request
    ) {

        FinancialSummaryDTO summary = financialService.generateSummary();

        return okResponse(
                "Resumo financeiro gerado",
                "FINANCIAL_SUMMARY_FOUND",
                FinancialSummaryResponse.fromDto(summary),
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