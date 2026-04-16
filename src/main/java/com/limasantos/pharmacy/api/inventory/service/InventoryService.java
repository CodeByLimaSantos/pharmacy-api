package com.limasantos.pharmacy.api.inventory.service;

import com.limasantos.pharmacy.api.inventory.dto.CreateInventoryLotDTO;
import com.limasantos.pharmacy.api.inventory.dto.InventoryLotDTO;
import com.limasantos.pharmacy.api.inventory.entity.InventoryLot;
import com.limasantos.pharmacy.api.inventory.entity.InventoryMovements;
import com.limasantos.pharmacy.api.inventory.repository.InventoryLotRepository;
import com.limasantos.pharmacy.api.inventory.repository.InventoryMovementRepository;
import com.limasantos.pharmacy.api.product.entity.Product;
import com.limasantos.pharmacy.api.shared.enums.MovementType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryLotRepository lotRepository;
    private final InventoryMovementRepository movementRepository;
    private final com.limasantos.pharmacy.api.product.repository.ProductRepository productRepository;
    private final com.limasantos.pharmacy.api.inventory.mapper.InventoryLotMapper inventoryLotMapper;
    private final com.limasantos.pharmacy.api.inventory.mapper.InventoryMovementMapper inventoryMovementMapper;



    // REGISTRO DE MOVIMENTAÇÕES


     //CREATE Registers an inventory entry movement for a specific lot.
    @Transactional
    public InventoryMovements registerEntry(InventoryLot lot, Integer quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException("Entry quantity must be positive");


        }

        // Save the lot if it's new
        if (lot.getId() == null) {
            lotRepository.save(lot);
        }

        InventoryMovements movement = new InventoryMovements();
        movement.setInventoryLot(lot);
        movement.setMovementType(MovementType.ENTRY);
        movement.setQuantity(quantity);

        return movementRepository.save(movement);


    }


    // Legacy method for backward compatibility
    @Transactional
    @Deprecated(forRemoval = true)
    public InventoryMovements registrarEntrada(InventoryLot lot, Integer quantidade) {
        return registerEntry(lot, quantidade);
    }


     //Registers a sale exit movement.
     //Uses FEFO strategy (First Expire, First Out) to select the lot.
    @Transactional
    public InventoryMovements registerSaleExit(InventoryLot lot, Integer quantity) {

        if (quantity <= 0) {

            throw new IllegalArgumentException("Exit quantity must be positive");


        }

        int available = calculateAvailableQuantity(lot);
        if (quantity > available) {

            throw new IllegalArgumentException(

                String.format("Insufficient quantity in lot %s. Available: %d, Requested: %d",
                    lot.getLotNumber(), available, quantity)


            );
        }

        InventoryMovements movement = new InventoryMovements();
        movement.setInventoryLot(lot);
        movement.setMovementType(MovementType.SALE_EXIT);
        movement.setQuantity(quantity);

        return movementRepository.save(movement);

    }



    // Legacy method for backward compatibility
    @Transactional
    @Deprecated(forRemoval = true)
    public InventoryMovements registrarSaidaPorVenda(InventoryLot lot, Integer quantidade) {
        return registerSaleExit(lot, quantidade);
    }



    //Registers a positive inventory adjustment.
    @Transactional
    public InventoryMovements registerAdjustmentIn(InventoryLot lot, Integer quantity, String reason) {
        if (quantity <= 0) {

            throw new IllegalArgumentException("Adjustment quantity must be positive");

        }

        InventoryMovements movement = new InventoryMovements();
        movement.setInventoryLot(lot);
        movement.setMovementType(MovementType.ADJUSTMENT_IN);
        movement.setQuantity(quantity);
        movement.setReason(reason);

        return movementRepository.save(movement);


    }

    // Legacy method for backward compatibility
    @Transactional
    @Deprecated(forRemoval = true)
    public InventoryMovements registrarAjusteEntrada(InventoryLot lot, Integer quantity, String reason) {

        return registerAdjustmentIn(lot, quantity, reason);

    }




    //Registers a negative inventory adjustment.
     //Reason is mandatory.
    @Transactional
    public InventoryMovements registerAdjustmentOut(InventoryLot lot, Integer quantity, String reason) {
        if (quantity <= 0) {

            throw new IllegalArgumentException("Adjustment quantity must be positive");

        }
        if (reason == null || reason.isBlank()) {

            throw new IllegalArgumentException("Reason is mandatory for output adjustment");

        }

        int available = calculateAvailableQuantity(lot);

        if (quantity > available) {

            throw new IllegalArgumentException(
                String.format("Insufficient quantity in lot %s for adjustment. Available: %d",
                    lot.getLotNumber(), available)


            );
        }

        InventoryMovements movement = new InventoryMovements();
        movement.setInventoryLot(lot);
        movement.setMovementType(MovementType.ADJUSTMENT_OUT);
        movement.setQuantity(quantity);
        movement.setReason(reason);

        return movementRepository.save(movement);
    }





    // Legacy method for backward compatibility
    @Transactional
    @Deprecated(forRemoval = true)
    public InventoryMovements registrarAjusteSaida(InventoryLot lot, Integer quantidade, String motivo) {
        return registerAdjustmentOut(lot, quantidade, motivo);
    }

    //Registers a disposal movement for expired or damaged lots, Reason is mandatory.
    @Transactional
    public InventoryMovements registerDisposal(InventoryLot lot, Integer quantity, String reason) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Disposal quantity must be positive");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Reason is mandatory for disposal");
        }

        int available = calculateAvailableQuantity(lot);
        if (quantity > available) {
            throw new IllegalArgumentException(
                String.format("Insufficient quantity in lot %s for disposal. Available: %d",
                    lot.getLotNumber(), available)
            );
        }

        InventoryMovements movement = new InventoryMovements();
        movement.setInventoryLot(lot);
        movement.setMovementType(MovementType.DISPOSAL);
        movement.setQuantity(quantity);
        movement.setReason(reason);

        return movementRepository.save(movement);
    }

    // Legacy method for backward compatibility
    @Transactional
    @Deprecated(forRemoval = true)
    public InventoryMovements registrarDescarte(InventoryLot lot, Integer quantidade, String motivo) {
        return registerDisposal(lot, quantidade, motivo);
    }





    // ==========================================
    // CONSULTAS DE SALDO
    // ==========================================


    /**
     * Calculates the available quantity of a lot.
     * Formula: ENTRY + ADJUSTMENT_IN - SALE_EXIT - ADJUSTMENT_OUT - DISPOSAL
     */
    @Transactional(readOnly = true)
    public Integer calculateAvailableQuantity(InventoryLot lot) {
        List<InventoryMovements> movements = movementRepository.findByInventoryLot(lot);

        int entries = movements.stream()
            .filter(m -> m.getMovementType() == MovementType.ENTRY ||
                         m.getMovementType() == MovementType.ADJUSTMENT_IN)
            .mapToInt(InventoryMovements::getQuantity)
            .sum();

        int exits = movements.stream()
            .filter(m -> m.getMovementType() == MovementType.SALE_EXIT ||
                         m.getMovementType() == MovementType.ADJUSTMENT_OUT ||
                         m.getMovementType() == MovementType.DISPOSAL)
            .mapToInt(InventoryMovements::getQuantity)
            .sum();

        return entries - exits;
    }

    // Legacy method for backward compatibility
    @Transactional(readOnly = true)
    @Deprecated(forRemoval = true)
    public int calcularQuantidadeDisponivel(InventoryLot lot) {
        return calculateAvailableQuantity(lot);
    }




    /**
     * Calculates consolidated product stock (sum of all lots).
     * Optimized to use query on repository instead of loading all lots in memory.
     */
    @Transactional(readOnly = true)
    public Integer calculateProductStock(Long productId) {
        var product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
        
        List<InventoryLot> lots = lotRepository.findByProduct(product);
        return lots.stream()
            .mapToInt(this::calculateAvailableQuantity)
            .sum();


    }



    // Legacy method for backward compatibility
    @Transactional(readOnly = true)
    @Deprecated(forRemoval = true)
    public int calcularSaldoProduto(Long productId) {
        return calculateProductStock(productId);
    }




    /**
     * Registers sale exit of stock distributing by lots (FEFO).
     * Uses first-expire-first-out strategy for lot selection.
     * Optimized to avoid multiple repository calls.
     */

    @Transactional
    public void registerSaleExitByProduct(Long productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Exit quantity must be positive");
        }


        var product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        // Find valid and non-expired lots sorted by expiration date (FEFO)
        List<InventoryLot> availableLots = lotRepository.findByProduct(product).stream()
            .filter(lot -> lot.getExpirationDate().isAfter(LocalDate.now()))
            .filter(lot -> calculateAvailableQuantity(lot) > 0)
            .sorted(Comparator.comparing(InventoryLot::getExpirationDate))
            .toList();

        int remaining = quantity;
        for (InventoryLot lot : availableLots) {
            if (remaining <= 0) break;

            int available = calculateAvailableQuantity(lot);
            int exit = Math.min(remaining, available);
            registerSaleExit(lot, exit);
            remaining -= exit;
        }

        // If quantities are still missing after using all lots, throw error
        if (remaining > 0) {
            throw new IllegalStateException(
                String.format("Insufficient stock for %s. Available: %d, Requested: %d",
                    product.getName(), quantity - remaining, quantity)
            );
        }
    }

    // Legacy method for backward compatibility
    @Transactional
    @Deprecated(forRemoval = true)
    public void registerSaleExit(Long productId, Integer quantity) {
        registerSaleExitByProduct(productId, quantity);
    }

    // ==========================================
    // GESTÃO DE LOTES
    // ==========================================

    /**
     * Finds the best lot for sale using FEFO strategy.
     * (First Expire, First Out - lot that expires first comes out first).
     */
    @Transactional(readOnly = true)
    public Optional<InventoryLot> findBestLotForSale(Long productId, Integer neededQuantity) {
        var product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
        
        List<InventoryLot> lots = lotRepository.findByProduct(product);

        return lots.stream()
            .filter(lot -> calculateAvailableQuantity(lot) >= neededQuantity)
            .filter(lot -> lot.getExpirationDate().isAfter(LocalDate.now()))
            .min(Comparator.comparing(InventoryLot::getExpirationDate));
    }

    // Legacy method for backward compatibility
    @Transactional(readOnly = true)
    @Deprecated(forRemoval = true)
    public Optional<InventoryLot> buscarMelhorLoteParaVenda(Long productId, Integer quantidadeNecessaria) {
        return findBestLotForSale(productId, quantidadeNecessaria);
    }

    /**
     * Returns expired lots for disposal.
     */
    @Transactional(readOnly = true)
    public List<InventoryLot> findExpiredLots() {
        return lotRepository.findByExpirationDateBefore(LocalDate.now());
    }

    // Legacy method for backward compatibility
    @Transactional(readOnly = true)
    @Deprecated(forRemoval = true)
    public List<InventoryLot> buscarLotesVencidos() {
        return findExpiredLots();
    }

    /**
     * Returns lots that expire within a period (alert for upcoming expiration).
     */
    @Transactional(readOnly = true)
    public List<InventoryLot> findLotsExpiringIn(int days) {
        LocalDate today = LocalDate.now();
        LocalDate future = today.plusDays(days);
        return lotRepository.findByExpirationDateBetween(today, future);
    }

    // Legacy method for backward compatibility
    @Transactional(readOnly = true)
    @Deprecated(forRemoval = true)
    public List<InventoryLot> buscarLotesVencendoEm(int dias) {
        return findLotsExpiringIn(dias);
    }

    /**
     * Processes automatic disposal of expired lots.
     * Registers disposal movement for each lot with available stock.
     */
    @Transactional
    public List<InventoryMovements> processExpiredLots() {
        List<InventoryLot> expired = findExpiredLots();
        return expired.stream()
            .map(lot -> {
                int available = calculateAvailableQuantity(lot);
                if (available > 0) {
                    return registerDisposal(lot, available,
                        "Automatic disposal due to expiration: " + lot.getExpirationDate());
                }
                return null;
            })
            .filter(java.util.Objects::nonNull)
            .toList();
    }

    // Legacy method for backward compatibility
    @Transactional
    @Deprecated(forRemoval = true)
    public List<InventoryMovements> processarVencidos() {
        return processExpiredLots();
    }

    // ==========================================
    // HISTÓRICO E AUDITORIA
    // ==========================================

    /**
     * Returns complete history of movements for a lot.
     */
    @Transactional(readOnly = true)
    public List<InventoryMovements> findLotHistory(Long lotId) {
        InventoryLot lot = lotRepository.findById(lotId)
            .orElseThrow(() -> new IllegalArgumentException("Lot not found: " + lotId));
        return movementRepository.findByInventoryLot(lot);
    }

    // Legacy method for backward compatibility
    @Transactional(readOnly = true)
    @Deprecated(forRemoval = true)
    public List<InventoryMovements> buscarHistoricoLote(Long lotId) {
        return findLotHistory(lotId);
    }

    /**
     * Returns all movements with reason (for audit trail).
     */
    @Transactional(readOnly = true)
    public List<InventoryMovements> findMovementsWithReason() {
        return movementRepository.findByReasonIsNotNull();
    }

    /**
     * Cria um novo lote de inventário e registra a entrada inicial.
     */
    @Transactional
    public InventoryLotDTO createLot(CreateInventoryLotDTO dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + dto.getProductId()));

        InventoryLot lot = inventoryLotMapper.toEntity(dto, product);
        InventoryLot savedLot = lotRepository.save(lot);

        // Registrar entrada inicial do lote
        registerEntry(savedLot, dto.getQuantity());

        return inventoryLotMapper.toDTO(savedLot);
    }
}
