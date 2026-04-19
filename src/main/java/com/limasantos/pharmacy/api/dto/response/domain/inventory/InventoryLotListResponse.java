package com.limasantos.pharmacy.api.dto.response.domain.inventory;

import com.limasantos.pharmacy.api.inventory.dto.InventoryLotDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryLotListResponse {
    private Long id;
    private String productName;
    private String lotNumber;
    private LocalDate expirationDate;
    private Integer availableQuantity;

    public static InventoryLotListResponse fromDto(InventoryLotDTO dto) {
        return new InventoryLotListResponse(
                dto.getId(),
                dto.getProductName(),
                dto.getLotNumber(),
                dto.getExpirationDate(),
                dto.getAvailableQuantity()
        );
    }
}

