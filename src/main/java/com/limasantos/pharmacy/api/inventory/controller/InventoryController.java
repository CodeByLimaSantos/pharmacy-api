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
@RequestMapping("/inventory")
@Tag(name = "Inventário", description = "Gestão de lotes, estoque e movimentações de inventário")
public class InventoryController {

    private final InventoryService inventoryService;

    private final InventoryLotMapper inventoryLotMapper;

    private final InventoryMovementMapper inventoryMovementMapper;

    private final InventoryLotRepository inventoryLotRepository;


    public InventoryController(
            InventoryService inventoryService,
            InventoryLotMapper inventoryLotMapper,
            InventoryMovementMapper inventoryMovementMapper,
            InventoryLotRepository inventoryLotRepository
    ) {

        this.inventoryService = inventoryService;

        this.inventoryLotMapper = inventoryLotMapper;

        this.inventoryMovementMapper = inventoryMovementMapper;

        this.inventoryLotRepository = inventoryLotRepository;
    }


    //create lots
    @PostMapping("/create")
    @Operation(summary = "Criar lote",
            description = "Cria um novo lote de inventário para um produto.")
    public ResponseEntity<ApiResponse<InventoryLotResponse>> createLot(
            @Valid @RequestBody CreateInventoryLotDTO dto,
            HttpServletRequest request) {

        InventoryLotDTO created = inventoryService.createLot(dto);

        return createdResponse(
                "Lote criado com sucesso",
                "INVENTORY_LOT_CREATED",
                InventoryLotResponse.fromDto(created),
                request

        );

    }


    //search lots by id
    @GetMapping("/lots/{id}")
    @Operation(summary = "Buscar lote por ID", description = "Retorna dados do lote incluindo quantidade disponível calculada.")
    public ResponseEntity<ApiResponse<InventoryLotResponse>> findLotById(
            @PathVariable Long id,
            HttpServletRequest request) {

        InventoryLot lot = getInventoryLotOrThrow(id);

        Integer availableQuantity = inventoryService.calculateAvailableQuantity(lot);

        InventoryLotDTO dto = inventoryLotMapper.toDTO(lot);

        dto.setAvailableQuantity(availableQuantity);

        return okResponse(
                "Lote encontrado",
                "INVENTORY_LOT_FOUND",
                InventoryLotResponse.fromDto(dto),
                request

        );


    }


    @GetMapping("/lots") //keep this for backward compatibility, but redirect to /all
    @Operation(summary = "Listar lotes (raiz)", description = "Atalho para listagem de lotes no recurso raiz de inventário.")
    public ResponseEntity<PaginatedResponse<InventoryLotListResponse>> findAllLotsRoot(
            HttpServletRequest request
    ) {
        return findAllLots(request);
    }

    //list all lots with available stock
    @GetMapping("/all")

    @Operation(summary = "Listar lotes", description = "Retorna todos os lotes de inventário com saldos disponíveis.")
    public ResponseEntity<PaginatedResponse<InventoryLotListResponse>> findAllLots(
            HttpServletRequest request
    ) {

        List<InventoryLotListResponse> lots = inventoryLotRepository
                .findAll()
                .stream()
                .map(lot -> {

                    InventoryLotDTO dto = inventoryLotMapper.toDTO(lot);

                    dto.setAvailableQuantity(
                            inventoryService.calculateAvailableQuantity(lot));

                    return InventoryLotListResponse.fromDto(dto);
                })
                .toList();

        return okPaginatedResponse(
                "Lotes recuperados com sucesso",
                "INVENTORY_LOTS_FOUND",
                lots,
                request

        );

    }


    //get stock of a product by id
    @GetMapping("/product/{productId}/stock")
    @Operation(summary = "Consultar estoque do produto", description = "Calcula e retorna o estoque atual de um produto.")
    public ResponseEntity<ApiResponse<ProductStockResponse>> getProductStock(
            @PathVariable Long productId,
            HttpServletRequest request) {

        Integer stock = inventoryService.calculateProductStock(productId);

        return okResponse(
                "Estoque do produto recuperado",
                "PRODUCT_STOCK_FOUND",
                new ProductStockResponse(productId, stock),
                request

        );

    }


    //find best lot for sale by product id and quantity
    @GetMapping("/lots/best-sale")
    @Operation(summary = "Encontrar melhor lote para venda", description = "Seleciona o melhor lote para venda com base no produto e quantidade solicitada.")
    public ResponseEntity<ApiResponse<InventoryLotResponse>> findBestLotForSale(
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            HttpServletRequest request) {

        Optional<InventoryLot> bestLot = inventoryService.findBestLotForSale(productId, quantity);

        if (bestLot.isEmpty()) {

            return errorResponse(
                    HttpStatus.NOT_FOUND,
                    "Nenhum lote disponível para venda",
                    "INVENTORY_LOT_NOT_FOUND",
                    request

            );

        }

        InventoryLot lot = bestLot.get();
        Integer availableQuantity = inventoryService.calculateAvailableQuantity(lot);

        InventoryLotDTO dto = inventoryLotMapper.toDTO(lot);
        dto.setAvailableQuantity(availableQuantity);

        return okResponse(
                "Melhor lote para venda encontrado",
                "BEST_SALE_LOT_FOUND",
                InventoryLotResponse.fromDto(dto),
                request

        );

    }


    //list expired lots
    @GetMapping("/lots/expired")
    @Operation(summary = "Listar lotes vencidos", description = "Retorna lotes com data de validade expirada.")
    public ResponseEntity<PaginatedResponse<InventoryLotListResponse>> findExpiredLots(HttpServletRequest request) {

        List<InventoryLotListResponse> expiredLots = inventoryService.findExpiredLots()

                .stream()
                .map(inventoryLotMapper::toDTO)
                .map(InventoryLotListResponse::fromDto)
                .toList();


        return okPaginatedResponse("Lotes vencidos recuperados",
                "EXPIRED_LOTS_FOUND",
                expiredLots,
                request);

    }


    //list lots expiring within a certain number of days
    @GetMapping("/lots/expiring/{days}")
    @Operation(summary = "Listar lotes próximos do vencimento", description = "Retorna lotes que vencem dentro da janela de dias informada.")
    public ResponseEntity<PaginatedResponse<InventoryLotListResponse>> findLotsExpiringIn(
            @PathVariable int days,
            HttpServletRequest request) {

        List<InventoryLotListResponse> expiringLots = inventoryService.findLotsExpiringIn(days)

                .stream()
                .map(inventoryLotMapper::toDTO)
                .map(InventoryLotListResponse::fromDto)
                .toList();

        return okPaginatedResponse("Lotes próximos do vencimento recuperados", "EXPIRING_LOTS_FOUND", expiringLots, request);


    }


    //list movements with reason for audit purposes
    @GetMapping("/movements/audit")
    @Operation(summary = "Listar movimentações para auditoria", description = "Retorna movimentações com motivo para fins de auditoria.")
    public ResponseEntity<PaginatedResponse<InventoryMovementListResponse>> findMovementsWithReason(
            HttpServletRequest request) {

        List<InventoryMovementListResponse> movements = inventoryMovementMapper.toDTOList(
                        inventoryService.findMovementsWithReason()

                ).stream()
                .map(InventoryMovementListResponse::fromDto)
                .toList();

        return okPaginatedResponse("Movimentações de auditoria recuperadas",
                "INVENTORY_MOVEMENTS_FOUND",
                movements,
                request);

    }


    //register entry, sale exit, adjustment in, adjustment out and disposal with reason
    @PostMapping("/lots/{lotId}/entry")
    @Operation(summary = "Registrar entrada", description = "Registra entrada de quantidade em um lote específico.")
    public ResponseEntity<ApiResponse<InventoryMovementResponse>> registerEntry(
            @PathVariable Long lotId,
            @RequestParam Integer quantity,
            HttpServletRequest request) {

        InventoryLot lot = getInventoryLotOrThrow(lotId);

        var movement = inventoryService.registerEntry(lot, quantity);

        return createdResponse(
                "Entrada registrada com sucesso",
                "INVENTORY_ENTRY_CREATED",
                InventoryMovementResponse.fromDto(inventoryMovementMapper.toDTO(movement)),
                request

        );
    }


    //register sale exit with reason
    @PostMapping("/lots/{lotId}/sale-exit")
    @Operation(summary = "Registrar saída por venda", description = "Registra saída de estoque por venda em um lote específico.")
    public ResponseEntity<ApiResponse<InventoryMovementResponse>> registerSaleExit(@PathVariable Long lotId,
                                                                                   @RequestParam Integer quantity,
                                                                                   HttpServletRequest request) {

        InventoryLot lot = getInventoryLotOrThrow(lotId);
        var movement = inventoryService.registerSaleExit(lot, quantity);
        return createdResponse(
                "Saída por venda registrada com sucesso",
                "INVENTORY_SALE_EXIT_CREATED",
                InventoryMovementResponse.fromDto(inventoryMovementMapper.toDTO(movement)),
                request
        );
    }


    //register adjustment in with reason
    @PostMapping("/lots/{lotId}/adjustment-in")
    @Operation(summary = "Registrar ajuste de entrada", description = "Registra ajuste positivo de estoque com motivo obrigatório.")
    public ResponseEntity<ApiResponse<InventoryMovementResponse>> registerAdjustmentIn(
            @PathVariable Long lotId,
            @RequestParam Integer quantity,
            @Parameter(description = "Justificativa do ajuste", required = true)
            @RequestParam String reason,
            HttpServletRequest request) {

        InventoryLot lot = getInventoryLotOrThrow(lotId);

        var movement = inventoryService.registerAdjustmentIn(lot, quantity, reason);

        return createdResponse(

                "Ajuste de entrada registrado com sucesso",
                "INVENTORY_ADJUSTMENT_IN_CREATED",
                InventoryMovementResponse.fromDto(inventoryMovementMapper.toDTO(movement)),
                request


        );


    }


    //register adjustment out with reason
    @PostMapping("/lots/{lotId}/adjustment-out")
    @Operation(summary = "Registrar ajuste de saída", description = "Registra ajuste negativo de estoque com motivo obrigatório.")
    public ResponseEntity<ApiResponse<InventoryMovementResponse>> registerAdjustmentOut(
            @PathVariable Long lotId,
            @RequestParam Integer quantity,
            @Parameter(description = "Justificativa do ajuste", required = true)
            @RequestParam String reason,
            HttpServletRequest request) {

        InventoryLot lot = getInventoryLotOrThrow(lotId);

        var movement = inventoryService.registerAdjustmentOut(lot, quantity, reason);

        return createdResponse(
                "Ajuste de saída registrado com sucesso",
                "INVENTORY_ADJUSTMENT_OUT_CREATED",
                InventoryMovementResponse.fromDto(inventoryMovementMapper.toDTO(movement)),
                request

        );


    }


    //register disposal with reason
    @PostMapping("/lots/{lotId}/disposal")
    @Operation(summary = "Registrar descarte", description = "Registra descarte de itens de um lote com motivo obrigatório.")
    public ResponseEntity<ApiResponse<InventoryMovementResponse>> registerDisposal(
            @PathVariable Long lotId,
            @RequestParam Integer quantity,
            @Parameter(description = "Motivo do descarte", required = true)
            @RequestParam String reason,
            HttpServletRequest request) {


        InventoryLot lot = getInventoryLotOrThrow(lotId);

        var movement = inventoryService.registerDisposal(lot, quantity, reason);

        return createdResponse(

                "Descarte registrado com sucesso",
                "INVENTORY_DISPOSAL_CREATED",
                InventoryMovementResponse.fromDto(inventoryMovementMapper.toDTO(movement)),
                request

        );

    }


    //get lot history with all movements
    @GetMapping("/lots/{lotId}/history")
    @Operation(summary = "Consultar histórico do lote", description = "Retorna histórico completo de movimentações de um lote.")
    public ResponseEntity<PaginatedResponse<InventoryMovementListResponse>> findLotHistory(
            @PathVariable Long lotId,
            HttpServletRequest request) {

        List<InventoryMovementListResponse> history = inventoryMovementMapper.toDTOList(
                        inventoryService.findLotHistory(lotId)
                ).stream()
                .map(InventoryMovementListResponse::fromDto)
                .toList();

        return okPaginatedResponse("Histórico do lote recuperado",
                "LOT_HISTORY_FOUND",
                history,
                request);
    }


    //process expired lots and generate disposals
    @PostMapping("/process-expired")
    @Operation(summary = "Processar lotes vencidos", description = "Executa processamento de lotes vencidos e retorna movimentações geradas.")
    public ResponseEntity<PaginatedResponse<InventoryMovementListResponse>> processExpiredLots(HttpServletRequest request) {

        List<InventoryMovementListResponse> disposals = inventoryMovementMapper.toDTOList(
                        inventoryService.processExpiredLots()
                ).stream()
                .map(InventoryMovementListResponse::fromDto)
                .toList();

        return okPaginatedResponse("Processamento de lotes vencidos concluído",
                "EXPIRED_LOTS_PROCESSED",
                disposals,
                request);


    }


    /// methods to build responses and handle common logic
    private <T> ResponseEntity<ApiResponse<T>> createdResponse(
            String message,
            String code,
            T data,
            HttpServletRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(buildApiResponse(true, message, code, data, request));

    }


    private <T> ResponseEntity<ApiResponse<T>> okResponse(
            String message,
            String code,
            T data,
            HttpServletRequest request
    ) {

        return ResponseEntity
                .ok(buildApiResponse(true, message, code, data, request));

    }


    private <T> ResponseEntity<ApiResponse<T>> errorResponse(
            HttpStatus status,
            String message,
            String code,
            HttpServletRequest request
    ) {

        return ResponseEntity
                .status(status)
                .body(buildApiResponse(false, message, code, null, request));

    }


    private <T> ApiResponse<T> buildApiResponse(
            boolean success,
            String message,
            String code,
            T data,
            HttpServletRequest request
    ) {

        return ApiResponse.<T>builder()
                .success(success)
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


    private InventoryLot getInventoryLotOrThrow(Long id) {

        return inventoryLotRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Lote de inventário não encontrado com ID: " + id
                        )
                );

    }


    private String resolvePath(HttpServletRequest request) {

        String queryString = request.getQueryString();

        return (queryString == null)
                ? request.getRequestURI()
                : request.getRequestURI() + "?" + queryString;

    }

}