package com.limasantos.pharmacy.api.dto.response.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteResponse {
    private Long id;
    private boolean deleted;
}

