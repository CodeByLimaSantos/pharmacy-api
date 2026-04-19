package com.limasantos.pharmacy.api.dto.response.domain.inventory;

import com.limasantos.pharmacy.api.inventory.dto.InventoryLotDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryLotResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String lotNumber;
    private LocalDate entryDate;
    private LocalDate expirationDate;
    private Integer quantity;
    private Integer availableQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static InventoryLotResponse fromDto(InventoryLotDTO dto) {
        return new InventoryLotResponse(
                dto.getId(),
                dto.getProductId(),
                dto.getProductName(),
                dto.getLotNumber(),
                dto.getEntryDate(),
                dto.getExpirationDate(),
                dto.getQuantity(),
                dto.getAvailableQuantity(),
                null,
                null
        );
    }
}

