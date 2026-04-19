package com.limasantos.pharmacy.api.inventory.controller;

import com.limasantos.pharmacy.api.dto.response.base.ApiResponse;
import com.limasantos.pharmacy.api.dto.response.base.PaginatedResponse;
import com.limasantos.pharmacy.api.dto.response.domain.inventory.InventoryLotListResponse;
import com.limasantos.pharmacy.api.dto.response.domain.inventory.InventoryLotResponse;
import com.limasantos.pharmacy.api.dto.response.domain.inventory.InventoryMovementListResponse;
import com.limasantos.pharmacy.api.dto.response.domain.inventory.InventoryMovementResponse;
import com.limasantos.pharmacy.api.dto.response.domain.inventory.ProductStockResponse;
import com.limasantos.pharmacy.api.inventory.dto.CreateInventoryLotDTO;
import com.limasantos.pharmacy.api.inventory.dto.InventoryLotDTO;
import com.limasantos.pharmacy.api.inventory.entity.InventoryLot;
import com.limasantos.pharmacy.api.inventory.mapper.InventoryLotMapper;
import com.limasantos.pharmacy.api.inventory.mapper.InventoryMovementMapper;
import com.limasantos.pharmacy.api.inventory.repository.InventoryLotRepository;
import com.limasantos.pharmacy.api.inventory.service.InventoryService;
import com.limasantos.pharmacy.api.shared.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/inventory")
@Tag(name = "Inventário", description = "Gestão de lotes, estoque e movimentações de inventário")
public class InventoryController {

    private final InventoryService inventoryService;
    private final InventoryLotMapper inventoryLotMapper;
    private final InventoryMovementMapper inventoryMovementMapper;
    private final InventoryLotRepository inventoryLotRepository;

    public InventoryController(InventoryService inventoryService,
                               InventoryLotMapper inventoryLotMapper,
                               InventoryMovementMapper inventoryMovementMapper,
                               InventoryLotRepository inventoryLotRepository) {
        this.inventoryService = inventoryService;
        this.inventoryLotMapper = inventoryLotMapper;
        this.inventoryMovementMapper = inventoryMovementMapper;
        this.inventoryLotRepository = inventoryLotRepository;
    }

    @PostMapping("/lots")
    @Operation(summary = "Criar lote", description = "Cria um novo lote de inventário para um produto.")
    public ResponseEntity<ApiResponse<InventoryLotResponse>> createLot(@Valid @RequestBody CreateInventoryLotDTO dto,
                                                                       HttpServletRequest request) {
        InventoryLotDTO created = inventoryService.createLot(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<InventoryLotResponse>builder()
                        .success(true)
                        .message("Lote criado com sucesso")
                        .code("INVENTORY_LOT_CREATED")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(InventoryLotResponse.fromDto(created))
                        .build()
        );
    }

    @GetMapping("/lots/{id}")
    @Operation(summary = "Buscar lote por ID", description = "Retorna dados do lote incluindo quantidade disponível calculada.")
    public ResponseEntity<ApiResponse<InventoryLotResponse>> findLotById(@PathVariable Long id,
                                                                          HttpServletRequest request) {
        InventoryLot lot = getInventoryLotOrThrow(id);
        Integer availableQuantity = inventoryService.calculateAvailableQuantity(lot);

        InventoryLotDTO dto = inventoryLotMapper.toDTO(lot);
        dto.setAvailableQuantity(availableQuantity);

        return ResponseEntity.ok(
                ApiResponse.<InventoryLotResponse>builder()
                        .success(true)
                        .message("Lote encontrado")
                        .code("INVENTORY_LOT_FOUND")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(InventoryLotResponse.fromDto(dto))
                        .build()
        );
    }

    @GetMapping("/lots")
    @Operation(summary = "Listar lotes", description = "Retorna todos os lotes de inventário com saldos disponíveis.")
    public ResponseEntity<PaginatedResponse<InventoryLotListResponse>> findAllLots(HttpServletRequest request) {
        List<InventoryLotListResponse> lots = inventoryLotRepository.findAll()
                .stream()
                .map(lot -> {
                    InventoryLotDTO dto = inventoryLotMapper.toDTO(lot);
                    dto.setAvailableQuantity(inventoryService.calculateAvailableQuantity(lot));
                    return InventoryLotListResponse.fromDto(dto);
                })
                .toList();
        return ResponseEntity.ok(
                PaginatedResponse.<InventoryLotListResponse>paginatedBuilder()
                        .success(true)
                        .message("Lotes recuperados com sucesso")
                        .code("INVENTORY_LOTS_FOUND")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(lots)
                        .page(0)
                        .pageSize(lots.size())
                        .totalElements(lots.size())
                        .totalPages(lots.isEmpty() ? 0 : 1)
                        .hasNext(false)
                        .hasPrevious(false)
                        .build()
        );
    }

    @GetMapping("/product/{productId}/stock")
    @Operation(summary = "Consultar estoque do produto", description = "Calcula e retorna o estoque atual de um produto.")
    public ResponseEntity<ApiResponse<ProductStockResponse>> getProductStock(@PathVariable Long productId,
                                                                             HttpServletRequest request) {
        Integer stock = inventoryService.calculateProductStock(productId);
        return ResponseEntity.ok(
                ApiResponse.<ProductStockResponse>builder()
                        .success(true)
                        .message("Estoque do produto recuperado")
                        .code("PRODUCT_STOCK_FOUND")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(new ProductStockResponse(productId, stock))
                        .build()
        );
    }

    @GetMapping("/lots/best-sale")
    @Operation(summary = "Encontrar melhor lote para venda", description = "Seleciona o melhor lote para venda com base no produto e quantidade solicitada.")
    public ResponseEntity<ApiResponse<InventoryLotResponse>> findBestLotForSale(@RequestParam Long productId,
                                                                                 @RequestParam Integer quantity,
                                                                                 HttpServletRequest request) {

        Optional<InventoryLot> bestLot = inventoryService.findBestLotForSale(productId, quantity);

        if (bestLot.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.<InventoryLotResponse>builder()
                            .success(false)
                            .message("Nenhum lote disponível para venda")
                            .code("INVENTORY_LOT_NOT_FOUND")
                            .timestamp(LocalDateTime.now())
                            .path(resolvePath(request))
                            .data(null)
                            .build()
            );
        }

        InventoryLot lot = bestLot.get();
        Integer availableQuantity = inventoryService.calculateAvailableQuantity(lot);

        InventoryLotDTO dto = inventoryLotMapper.toDTO(lot);
        dto.setAvailableQuantity(availableQuantity);

        return ResponseEntity.ok(
                ApiResponse.<InventoryLotResponse>builder()
                        .success(true)
                        .message("Melhor lote para venda encontrado")
                        .code("BEST_SALE_LOT_FOUND")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(InventoryLotResponse.fromDto(dto))
                        .build()
        );
    }

    @GetMapping("/lots/expired")
    @Operation(summary = "Listar lotes vencidos", description = "Retorna lotes com data de validade expirada.")
    public ResponseEntity<PaginatedResponse<InventoryLotListResponse>> findExpiredLots(HttpServletRequest request) {
        List<InventoryLotListResponse> expiredLots = inventoryService.findExpiredLots()
                .stream()
                .map(inventoryLotMapper::toDTO)
                .map(InventoryLotListResponse::fromDto)
                .toList();
        return ResponseEntity.ok(
                PaginatedResponse.<InventoryLotListResponse>paginatedBuilder()
                        .success(true)
                        .message("Lotes vencidos recuperados")
                        .code("EXPIRED_LOTS_FOUND")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(expiredLots)
                        .page(0)
                        .pageSize(expiredLots.size())
                        .totalElements(expiredLots.size())
                        .totalPages(expiredLots.isEmpty() ? 0 : 1)
                        .hasNext(false)
                        .hasPrevious(false)
                        .build()
        );
    }

    @GetMapping("/lots/expiring/{days}")
    @Operation(summary = "Listar lotes próximos do vencimento", description = "Retorna lotes que vencem dentro da janela de dias informada.")
    public ResponseEntity<PaginatedResponse<InventoryLotListResponse>> findLotsExpiringIn(@PathVariable int days,
                                                                                            HttpServletRequest request) {
        List<InventoryLotListResponse> expiringLots = inventoryService.findLotsExpiringIn(days)
                .stream()
                .map(inventoryLotMapper::toDTO)
                .map(InventoryLotListResponse::fromDto)
                .toList();
        return ResponseEntity.ok(
                PaginatedResponse.<InventoryLotListResponse>paginatedBuilder()
                        .success(true)
                        .message("Lotes próximos do vencimento recuperados")
                        .code("EXPIRING_LOTS_FOUND")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(expiringLots)
                        .page(0)
                        .pageSize(expiringLots.size())
                        .totalElements(expiringLots.size())
                        .totalPages(expiringLots.isEmpty() ? 0 : 1)
                        .hasNext(false)
                        .hasPrevious(false)
                        .build()
        );
    }

    @GetMapping("/movements/audit")
    @Operation(summary = "Listar movimentações para auditoria", description = "Retorna movimentações com motivo para fins de auditoria.")
    public ResponseEntity<PaginatedResponse<InventoryMovementListResponse>> findMovementsWithReason(HttpServletRequest request) {
        List<InventoryMovementListResponse> movements = inventoryMovementMapper.toDTOList(
                        inventoryService.findMovementsWithReason()
                ).stream()
                .map(InventoryMovementListResponse::fromDto)
                .toList();
        return ResponseEntity.ok(
                PaginatedResponse.<InventoryMovementListResponse>paginatedBuilder()
                        .success(true)
                        .message("Movimentações de auditoria recuperadas")
                        .code("INVENTORY_MOVEMENTS_FOUND")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(movements)
                        .page(0)
                        .pageSize(movements.size())
                        .totalElements(movements.size())
                        .totalPages(movements.isEmpty() ? 0 : 1)
                        .hasNext(false)
                        .hasPrevious(false)
                        .build()
        );
    }

    @PostMapping("/lots/{lotId}/entry")
    @Operation(summary = "Registrar entrada", description = "Registra entrada de quantidade em um lote específico.")
    public ResponseEntity<ApiResponse<InventoryMovementResponse>> registerEntry(@PathVariable Long lotId,
                                                                                 @RequestParam Integer quantity,
                                                                                 HttpServletRequest request) {

        InventoryLot lot = getInventoryLotOrThrow(lotId);
        var movement = inventoryService.registerEntry(lot, quantity);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<InventoryMovementResponse>builder()
                        .success(true)
                        .message("Entrada registrada com sucesso")
                        .code("INVENTORY_ENTRY_CREATED")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(InventoryMovementResponse.fromDto(inventoryMovementMapper.toDTO(movement)))
                        .build()
        );
    }

    @PostMapping("/lots/{lotId}/sale-exit")
    @Operation(summary = "Registrar saída por venda", description = "Registra saída de estoque por venda em um lote específico.")
    public ResponseEntity<ApiResponse<InventoryMovementResponse>> registerSaleExit(@PathVariable Long lotId,
                                                                                    @RequestParam Integer quantity,
                                                                                    HttpServletRequest request) {

        InventoryLot lot = getInventoryLotOrThrow(lotId);
        var movement = inventoryService.registerSaleExit(lot, quantity);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<InventoryMovementResponse>builder()
                        .success(true)
                        .message("Saída por venda registrada com sucesso")
                        .code("INVENTORY_SALE_EXIT_CREATED")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(InventoryMovementResponse.fromDto(inventoryMovementMapper.toDTO(movement)))
                        .build()
        );
    }

    @PostMapping("/lots/{lotId}/adjustment-in")
    @Operation(summary = "Registrar ajuste de entrada", description = "Registra ajuste positivo de estoque com motivo obrigatório.")
    public ResponseEntity<ApiResponse<InventoryMovementResponse>> registerAdjustmentIn(@PathVariable Long lotId,
                                                                                        @RequestParam Integer quantity,
                                                                                        @Parameter(description = "Justificativa do ajuste", required = true)
                                                                                        @RequestParam String reason,
                                                                                        HttpServletRequest request) {

        InventoryLot lot = getInventoryLotOrThrow(lotId);
        var movement = inventoryService.registerAdjustmentIn(lot, quantity, reason);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<InventoryMovementResponse>builder()
                        .success(true)
                        .message("Ajuste de entrada registrado com sucesso")
                        .code("INVENTORY_ADJUSTMENT_IN_CREATED")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(InventoryMovementResponse.fromDto(inventoryMovementMapper.toDTO(movement)))
                        .build()
        );
    }

    @PostMapping("/lots/{lotId}/adjustment-out")
    @Operation(summary = "Registrar ajuste de saída", description = "Registra ajuste negativo de estoque com motivo obrigatório.")
    public ResponseEntity<ApiResponse<InventoryMovementResponse>> registerAdjustmentOut(@PathVariable Long lotId,
                                                                                         @RequestParam Integer quantity,
                                                                                         @Parameter(description = "Justificativa do ajuste", required = true)
                                                                                         @RequestParam String reason,
                                                                                         HttpServletRequest request) {

        InventoryLot lot = getInventoryLotOrThrow(lotId);
        var movement = inventoryService.registerAdjustmentOut(lot, quantity, reason);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<InventoryMovementResponse>builder()
                        .success(true)
                        .message("Ajuste de saída registrado com sucesso")
                        .code("INVENTORY_ADJUSTMENT_OUT_CREATED")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(InventoryMovementResponse.fromDto(inventoryMovementMapper.toDTO(movement)))
                        .build()
        );
    }

    @PostMapping("/lots/{lotId}/disposal")
    @Operation(summary = "Registrar descarte", description = "Registra descarte de itens de um lote com motivo obrigatório.")
    public ResponseEntity<ApiResponse<InventoryMovementResponse>> registerDisposal(@PathVariable Long lotId,
                                                                                    @RequestParam Integer quantity,
                                                                                    @Parameter(description = "Motivo do descarte", required = true)
                                                                                    @RequestParam String reason,
                                                                                    HttpServletRequest request) {

        InventoryLot lot = getInventoryLotOrThrow(lotId);
        var movement = inventoryService.registerDisposal(lot, quantity, reason);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<InventoryMovementResponse>builder()
                        .success(true)
                        .message("Descarte registrado com sucesso")
                        .code("INVENTORY_DISPOSAL_CREATED")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(InventoryMovementResponse.fromDto(inventoryMovementMapper.toDTO(movement)))
                        .build()
        );
    }

    @GetMapping("/lots/{lotId}/history")
    @Operation(summary = "Consultar histórico do lote", description = "Retorna histórico completo de movimentações de um lote.")
    public ResponseEntity<PaginatedResponse<InventoryMovementListResponse>> findLotHistory(@PathVariable Long lotId,
                                                                                             HttpServletRequest request) {
        List<InventoryMovementListResponse> history = inventoryMovementMapper.toDTOList(
                        inventoryService.findLotHistory(lotId)
                ).stream()
                .map(InventoryMovementListResponse::fromDto)
                .toList();
        return ResponseEntity.ok(
                PaginatedResponse.<InventoryMovementListResponse>paginatedBuilder()
                        .success(true)
                        .message("Histórico do lote recuperado")
                        .code("LOT_HISTORY_FOUND")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(history)
                        .page(0)
                        .pageSize(history.size())
                        .totalElements(history.size())
                        .totalPages(history.isEmpty() ? 0 : 1)
                        .hasNext(false)
                        .hasPrevious(false)
                        .build()
        );
    }

    @PostMapping("/process-expired")
    @Operation(summary = "Processar lotes vencidos", description = "Executa processamento de lotes vencidos e retorna movimentações geradas.")
    public ResponseEntity<PaginatedResponse<InventoryMovementListResponse>> processExpiredLots(HttpServletRequest request) {
        List<InventoryMovementListResponse> disposals = inventoryMovementMapper.toDTOList(
                        inventoryService.processExpiredLots()
                ).stream()
                .map(InventoryMovementListResponse::fromDto)
                .toList();
        return ResponseEntity.ok(
                PaginatedResponse.<InventoryMovementListResponse>paginatedBuilder()
                        .success(true)
                        .message("Processamento de lotes vencidos concluído")
                        .code("EXPIRED_LOTS_PROCESSED")
                        .timestamp(LocalDateTime.now())
                        .path(resolvePath(request))
                        .data(disposals)
                        .page(0)
                        .pageSize(disposals.size())
                        .totalElements(disposals.size())
                        .totalPages(disposals.isEmpty() ? 0 : 1)
                        .hasNext(false)
                        .hasPrevious(false)
                        .build()
        );
    }

    private InventoryLot getInventoryLotOrThrow(Long id) {
        return inventoryLotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lote de inventário não encontrado com ID: " + id));
    }

    private String resolvePath(HttpServletRequest request) {
        String queryString = request.getQueryString();
        return queryString == null ? request.getRequestURI() : request.getRequestURI() + "?" + queryString;
    }
}
