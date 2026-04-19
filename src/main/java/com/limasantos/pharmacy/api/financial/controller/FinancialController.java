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
@RequestMapping("/api/financial")
@Tag(name = "Financeiro", description = "Operações de contas a pagar, contas a receber e consolidação financeira")
public class FinancialController {

    private final FinancialService financialService;

    public FinancialController(FinancialService financialService) {
        this.financialService = financialService;
    }



    @PostMapping
    @Operation(summary = "Criar lançamento financeiro", description = "Cria um novo lançamento financeiro com base nos dados informados.")

    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Lançamento criado", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Erro interno")
    })

    public ResponseEntity<ApiResponse<FinancialResponse>> create(@Valid @RequestBody CreateFinancialDTO dto,
                                                                 HttpServletRequest request) {
        FinancialDTO created = financialService.createFinancial(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<FinancialResponse>builder()
                        .success(true)
                        .message("Lançamento financeiro criado com sucesso")
                        .code("FINANCIAL_CREATED")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(FinancialResponse.fromDto(created))
                        .build()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar lançamento por ID", description = "Retorna um lançamento financeiro resumido pelo identificador.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lançamento encontrado", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Lançamento não encontrado")
    })
    public ResponseEntity<ApiResponse<FinancialResponse>> findById(@PathVariable Long id,
                                                                    HttpServletRequest request) {
        FinancialDTO financial = financialService.findById(id);
        return ResponseEntity.ok(
                ApiResponse.<FinancialResponse>builder()
                        .success(true)
                        .message("Lançamento financeiro encontrado")
                        .code("FINANCIAL_FOUND")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(FinancialResponse.fromDto(financial))
                        .build()
        );
    }

    @GetMapping("/{id}/detail")
    @Operation(summary = "Buscar detalhes do lançamento", description = "Retorna os dados detalhados de um lançamento financeiro por ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Detalhes encontrados", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Lançamento não encontrado")
    })
    public ResponseEntity<ApiResponse<FinancialDetailDTO>> findDetailById(@PathVariable Long id,
                                                                           HttpServletRequest request) {
        FinancialDetailDTO detail = financialService.findDetailById(id);
        return ResponseEntity.ok(
                ApiResponse.<FinancialDetailDTO>builder()
                        .success(true)
                        .message("Detalhes do lançamento financeiro encontrados")
                        .code("FINANCIAL_DETAIL_FOUND")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(detail)
                        .build()
        );
    }


    @GetMapping
    @Operation(summary = "Listar lançamentos financeiros", description = "Retorna todos os lançamentos financeiros no envelope paginado da API.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista carregada", content = @Content(schema = @Schema(implementation = PaginatedResponse.class)))
    })
    public ResponseEntity<PaginatedResponse<FinancialListResponse>> findAll(HttpServletRequest request) {
        List<FinancialListResponse> financials = financialService.findAll().stream()
                .map(FinancialListResponse::fromDto)
                .toList();
        return ResponseEntity.ok(
                PaginatedResponse.<FinancialListResponse>paginatedBuilder()
                        .success(true)
                        .message("Lançamentos financeiros recuperados")
                        .code("FINANCIALS_FOUND")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(financials)
                        .page(0)
                        .pageSize(financials.size())
                        .totalElements(financials.size())
                        .totalPages(financials.isEmpty() ? 0 : 1)
                        .hasNext(false)
                        .hasPrevious(false)
                        .build()
        );
    }

    @GetMapping("/pending")
    @Operation(summary = "Listar lançamentos pendentes", description = "Retorna lançamentos financeiros com status pendente.")
    public ResponseEntity<PaginatedResponse<FinancialListResponse>> findPending(HttpServletRequest request) {
        List<FinancialListResponse> pending = financialService.findPending().stream()
                .map(FinancialListResponse::fromDto)
                .toList();
        return ResponseEntity.ok(
                PaginatedResponse.<FinancialListResponse>paginatedBuilder()
                        .success(true)
                        .message("Pendências financeiras recuperadas")
                        .code("FINANCIAL_PENDING_FOUND")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(pending)
                        .page(0)
                        .pageSize(pending.size())
                        .totalElements(pending.size())
                        .totalPages(pending.isEmpty() ? 0 : 1)
                        .hasNext(false)
                        .hasPrevious(false)
                        .build()
        );
    }

    @GetMapping("/overdue")
    @Operation(summary = "Listar lançamentos vencidos", description = "Retorna lançamentos com vencimento expirado e ainda não quitados.")
    public ResponseEntity<PaginatedResponse<FinancialListResponse>> findOverdue(HttpServletRequest request) {
        List<FinancialListResponse> overdue = financialService.findOverdue().stream()
                .map(FinancialListResponse::fromDto)
                .toList();
        return ResponseEntity.ok(
                PaginatedResponse.<FinancialListResponse>paginatedBuilder()
                        .success(true)
                        .message("Lançamentos vencidos recuperados")
                        .code("FINANCIAL_OVERDUE_FOUND")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(overdue)
                        .page(0)
                        .pageSize(overdue.size())
                        .totalElements(overdue.size())
                        .totalPages(overdue.isEmpty() ? 0 : 1)
                        .hasNext(false)
                        .hasPrevious(false)
                        .build()
        );
    }

    @GetMapping("/receivable")
    @Operation(summary = "Listar contas a receber", description = "Retorna lançamentos classificados como contas a receber.")
    public ResponseEntity<PaginatedResponse<FinancialListResponse>> findReceivable(HttpServletRequest request) {
        List<FinancialListResponse> receivable = financialService.findReceivable().stream()
                .map(FinancialListResponse::fromDto)
                .toList();
        return ResponseEntity.ok(
                PaginatedResponse.<FinancialListResponse>paginatedBuilder()
                        .success(true)
                        .message("Contas a receber recuperadas")
                        .code("FINANCIAL_RECEIVABLE_FOUND")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(receivable)
                        .page(0)
                        .pageSize(receivable.size())
                        .totalElements(receivable.size())
                        .totalPages(receivable.isEmpty() ? 0 : 1)
                        .hasNext(false)
                        .hasPrevious(false)
                        .build()
        );
    }

    @GetMapping("/payable")
    @Operation(summary = "Listar contas a pagar", description = "Retorna lançamentos classificados como contas a pagar.")
    public ResponseEntity<PaginatedResponse<FinancialListResponse>> findPayable(HttpServletRequest request) {
        List<FinancialListResponse> payable = financialService.findPayable().stream()
                .map(FinancialListResponse::fromDto)
                .toList();
        return ResponseEntity.ok(
                PaginatedResponse.<FinancialListResponse>paginatedBuilder()
                        .success(true)
                        .message("Contas a pagar recuperadas")
                        .code("FINANCIAL_PAYABLE_FOUND")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(payable)
                        .page(0)
                        .pageSize(payable.size())
                        .totalElements(payable.size())
                        .totalPages(payable.isEmpty() ? 0 : 1)
                        .hasNext(false)
                        .hasPrevious(false)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar lançamento", description = "Atualiza um lançamento financeiro existente pelo ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lançamento atualizado", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Lançamento não encontrado")
    })
    public ResponseEntity<ApiResponse<FinancialResponse>> update(@PathVariable Long id,
                                                                 @Valid @RequestBody CreateFinancialDTO dto,
                                                                 HttpServletRequest request) {
        FinancialDTO updated = financialService.update(id, dto);
        return ResponseEntity.ok(
                ApiResponse.<FinancialResponse>builder()
                        .success(true)
                        .message("Lançamento financeiro atualizado")
                        .code("FINANCIAL_UPDATED")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(FinancialResponse.fromDto(updated))
                        .build()
        );
    }

    @PostMapping("/{id}/pay")
    @Operation(summary = "Marcar lançamento como pago", description = "Define o método de pagamento e altera o status do lançamento para pago.")
    public ResponseEntity<ApiResponse<FinancialResponse>> markAsPaid(@PathVariable Long id,
                                                                      @Parameter(description = "Método utilizado para pagamento", required = true)
                                                                      @RequestParam PaymentMethod paymentMethod,
                                                                      HttpServletRequest request) {
        FinancialDTO paid = financialService.markAsPaid(id, paymentMethod);
        return ResponseEntity.ok(
                ApiResponse.<FinancialResponse>builder()
                        .success(true)
                        .message("Lançamento marcado como pago")
                        .code("FINANCIAL_MARKED_PAID")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(FinancialResponse.fromDto(paid))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover lançamento", description = "Remove logicamente/fisicamente um lançamento financeiro conforme regra de negócio.")
    public ResponseEntity<ApiResponse<DeleteResponse>> delete(@PathVariable Long id,
                                                               HttpServletRequest request) {
        financialService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.<DeleteResponse>builder()
                        .success(true)
                        .message("Lançamento financeiro removido")
                        .code("FINANCIAL_DELETED")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(new DeleteResponse(id, true))
                        .build()
        );
    }

    @GetMapping("/summary")
    @Operation(summary = "Gerar resumo financeiro", description = "Consolida totais, pendências e vencimentos do financeiro em um resumo único.")
    public ResponseEntity<ApiResponse<FinancialSummaryResponse>> generateSummary(HttpServletRequest request) {
        FinancialSummaryDTO summary = financialService.generateSummary();
        return ResponseEntity.ok(
                ApiResponse.<FinancialSummaryResponse>builder()
                        .success(true)
                        .message("Resumo financeiro gerado")
                        .code("FINANCIAL_SUMMARY_FOUND")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(FinancialSummaryResponse.fromDto(summary))
                        .build()
        );
    }

    private String resolvePath(HttpServletRequest request) {
        String queryString = request.getQueryString();
        return queryString == null ? request.getRequestURI() : request.getRequestURI() + "?" + queryString;
    }
}
