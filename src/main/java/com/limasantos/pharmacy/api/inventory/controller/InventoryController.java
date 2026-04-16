package com.limasantos.pharmacy.api.inventory.controller;

import com.limasantos.pharmacy.api.inventory.dto.CreateInventoryLotDTO;
import com.limasantos.pharmacy.api.inventory.dto.InventoryLotDTO;
import com.limasantos.pharmacy.api.inventory.dto.InventoryMovementDTO;
import com.limasantos.pharmacy.api.inventory.entity.InventoryLot;
import com.limasantos.pharmacy.api.inventory.mapper.InventoryLotMapper;
import com.limasantos.pharmacy.api.inventory.mapper.InventoryMovementMapper;
import com.limasantos.pharmacy.api.inventory.repository.InventoryLotRepository;
import com.limasantos.pharmacy.api.inventory.service.InventoryService;
import com.limasantos.pharmacy.api.shared.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/inventory")
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
    public ResponseEntity<InventoryLotDTO> createLot(@Valid @RequestBody CreateInventoryLotDTO dto) {
        InventoryLotDTO created = inventoryService.createLot(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/lots/{id}")
    public ResponseEntity<InventoryLotDTO> findLotById(@PathVariable Long id) {
        InventoryLot lot = getInventoryLotOrThrow(id);
        Integer availableQuantity = inventoryService.calculateAvailableQuantity(lot);

        InventoryLotDTO dto = inventoryLotMapper.toDTO(lot);
        dto.setAvailableQuantity(availableQuantity);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/lots")
    public ResponseEntity<List<InventoryLotDTO>> findAllLots() {
        List<InventoryLotDTO> lots = inventoryLotRepository.findAll()
                .stream()
                .map(lot -> {
                    InventoryLotDTO dto = inventoryLotMapper.toDTO(lot);
                    dto.setAvailableQuantity(inventoryService.calculateAvailableQuantity(lot));
                    return dto;
                })
                .toList();
        return ResponseEntity.ok(lots);
    }

    @GetMapping("/product/{productId}/stock")
    public ResponseEntity<Integer> getProductStock(@PathVariable Long productId) {
        Integer stock = inventoryService.calculateProductStock(productId);
        return ResponseEntity.ok(stock);
    }

    @GetMapping("/lots/best-sale")
    public ResponseEntity<InventoryLotDTO> findBestLotForSale(
            @RequestParam Long productId,
            @RequestParam Integer quantity) {

        Optional<InventoryLot> bestLot = inventoryService.findBestLotForSale(productId, quantity);

        if (bestLot.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        InventoryLot lot = bestLot.get();
        Integer availableQuantity = inventoryService.calculateAvailableQuantity(lot);

        InventoryLotDTO dto = inventoryLotMapper.toDTO(lot);
        dto.setAvailableQuantity(availableQuantity);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/lots/expired")
    public ResponseEntity<List<InventoryLotDTO>> findExpiredLots() {
        List<InventoryLotDTO> expiredLots = inventoryService.findExpiredLots()
                .stream()
                .map(inventoryLotMapper::toDTO)
                .toList();
        return ResponseEntity.ok(expiredLots);
    }

    @GetMapping("/lots/expiring/{days}")
    public ResponseEntity<List<InventoryLotDTO>> findLotsExpiringIn(@PathVariable int days) {
        List<InventoryLotDTO> expiringLots = inventoryService.findLotsExpiringIn(days)
                .stream()
                .map(inventoryLotMapper::toDTO)
                .toList();
        return ResponseEntity.ok(expiringLots);
    }

    @GetMapping("/movements/audit")
    public ResponseEntity<List<InventoryMovementDTO>> findMovementsWithReason() {
        List<InventoryMovementDTO> movements = inventoryMovementMapper.toDTOList(
                inventoryService.findMovementsWithReason()
        );
        return ResponseEntity.ok(movements);
    }

    @PostMapping("/lots/{lotId}/entry")
    public ResponseEntity<InventoryMovementDTO> registerEntry(
            @PathVariable Long lotId,
            @RequestParam Integer quantity) {

        InventoryLot lot = getInventoryLotOrThrow(lotId);
        var movement = inventoryService.registerEntry(lot, quantity);
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryMovementMapper.toDTO(movement));
    }

    @PostMapping("/lots/{lotId}/sale-exit")
    public ResponseEntity<InventoryMovementDTO> registerSaleExit(
            @PathVariable Long lotId,
            @RequestParam Integer quantity) {

        InventoryLot lot = getInventoryLotOrThrow(lotId);
        var movement = inventoryService.registerSaleExit(lot, quantity);
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryMovementMapper.toDTO(movement));
    }

    @PostMapping("/lots/{lotId}/adjustment-in")
    public ResponseEntity<InventoryMovementDTO> registerAdjustmentIn(
            @PathVariable Long lotId,
            @RequestParam Integer quantity,
            @RequestParam String reason) {

        InventoryLot lot = getInventoryLotOrThrow(lotId);
        var movement = inventoryService.registerAdjustmentIn(lot, quantity, reason);
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryMovementMapper.toDTO(movement));
    }

    @PostMapping("/lots/{lotId}/adjustment-out")
    public ResponseEntity<InventoryMovementDTO> registerAdjustmentOut(
            @PathVariable Long lotId,
            @RequestParam Integer quantity,
            @RequestParam String reason) {

        InventoryLot lot = getInventoryLotOrThrow(lotId);
        var movement = inventoryService.registerAdjustmentOut(lot, quantity, reason);
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryMovementMapper.toDTO(movement));
    }

    @PostMapping("/lots/{lotId}/disposal")
    public ResponseEntity<InventoryMovementDTO> registerDisposal(
            @PathVariable Long lotId,
            @RequestParam Integer quantity,
            @RequestParam String reason) {

        InventoryLot lot = getInventoryLotOrThrow(lotId);
        var movement = inventoryService.registerDisposal(lot, quantity, reason);
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryMovementMapper.toDTO(movement));
    }

    @GetMapping("/lots/{lotId}/history")
    public ResponseEntity<List<InventoryMovementDTO>> findLotHistory(@PathVariable Long lotId) {
        List<InventoryMovementDTO> history = inventoryMovementMapper.toDTOList(
                inventoryService.findLotHistory(lotId)
        );
        return ResponseEntity.ok(history);
    }

    @PostMapping("/process-expired")
    public ResponseEntity<List<InventoryMovementDTO>> processExpiredLots() {
        List<InventoryMovementDTO> disposals = inventoryMovementMapper.toDTOList(
                inventoryService.processExpiredLots()
        );
        return ResponseEntity.ok(disposals);
    }

    private InventoryLot getInventoryLotOrThrow(Long id) {
        return inventoryLotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lote de inventário não encontrado com ID: " + id));
    }
}
