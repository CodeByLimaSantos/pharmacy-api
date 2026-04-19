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
public class InventoryMovementListResponse {
    private Long id;
    private MovementType movementType;
    private Integer quantity;
    private LocalDateTime movementDate;

    public static InventoryMovementListResponse fromDto(InventoryMovementDTO dto) {
        return new InventoryMovementListResponse(
                dto.getId(),
                dto.getMovementType(),
                dto.getQuantity(),
                dto.getMovementDate()
        );
    }
}

