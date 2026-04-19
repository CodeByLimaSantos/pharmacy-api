package com.limasantos.pharmacy.api.dto.response.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String error;
    private String details;
    private LocalDateTime timestamp;
    private String path;
    private int httpStatus;
}

