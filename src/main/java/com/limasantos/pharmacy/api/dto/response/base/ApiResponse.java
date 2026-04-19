package com.limasantos.pharmacy.api.dto.response.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private String code;
    private LocalDateTime timestamp;
    private String path;
    private T data;

    public static <T> ApiResponseBuilder<T> builder() {
        return new ApiResponseBuilder<>();
    }

    public static class ApiResponseBuilder<T> {
        private boolean success;
        private String message;
        private String code;
        private LocalDateTime timestamp;
        private String path;
        private T data;

        public ApiResponseBuilder<T> success(boolean success) {
            this.success = success;
            return this;
        }

        public ApiResponseBuilder<T> message(String message) {
            this.message = message;
            return this;
        }

        public ApiResponseBuilder<T> code(String code) {
            this.code = code;
            return this;
        }

        public ApiResponseBuilder<T> timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ApiResponseBuilder<T> path(String path) {
            this.path = path;
            return this;
        }

        public ApiResponseBuilder<T> data(T data) {
            this.data = data;
            return this;
        }

        public ApiResponse<T> build() {
            return new ApiResponse<>(success, message, code, timestamp, path, data);
        }
    }
}

