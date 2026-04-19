package com.limasantos.pharmacy.api.dto.response.error;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ValidationErrorResponse extends ErrorResponse {

    private Map<String, String> fieldErrors;

    public ValidationErrorResponse(String error,
                                   String details,
                                   LocalDateTime timestamp,
                                   String path,
                                   int httpStatus,
                                   Map<String, String> fieldErrors) {
        super(error, details, timestamp, path, httpStatus);
        this.fieldErrors = fieldErrors;
    }
}

