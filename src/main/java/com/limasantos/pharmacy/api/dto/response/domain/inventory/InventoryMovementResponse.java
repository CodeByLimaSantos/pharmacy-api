package com.limasantos.pharmacy.api.dto.response.domain.inventory;

import com.limasantos.pharmacy.api.inventory.dto.InventoryMovementDTO;
import com.limasantos.pharmacy.api.shared.enums.MovementType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryMovementResponse {
    private Long id;
    private Long inventoryLotId;
    private String lotNumber;
    private Long productId;
    private String productName;
    private MovementType movementType;
    private Integer quantity;
    private LocalDateTime movementDate;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static InventoryMovementResponse fromDto(InventoryMovementDTO dto) {
        return new InventoryMovementResponse(
                dto.getId(),
                dto.getInventoryLotId(),
                dto.getLotNumber(),
                dto.getProductId(),
                dto.getProductName(),
                dto.getMovementType(),
                dto.getQuantity(),
                dto.getMovementDate(),
                dto.getReason(),
                null,
                null
        );
    }
}

