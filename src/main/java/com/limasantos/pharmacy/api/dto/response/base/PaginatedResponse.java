package com.limasantos.pharmacy.api.dto.response.base;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PaginatedResponse<T> extends ApiResponse<List<T>> {

    private int page;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;

    public PaginatedResponse(boolean success,
                             String message,
                             String code,
                             LocalDateTime timestamp,
                             String path,
                             List<T> data,
                             int page,
                             int pageSize,
                             long totalElements,
                             int totalPages,
                             boolean hasNext,
                             boolean hasPrevious) {
        super(success, message, code, timestamp, path, data);
        this.page = page;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
    }

    public static <T> PaginatedResponseBuilder<T> paginatedBuilder() {
        return new PaginatedResponseBuilder<>();
    }

    public static class PaginatedResponseBuilder<T> {
        private boolean success;
        private String message;
        private String code;
        private LocalDateTime timestamp;
        private String path;
        private List<T> data;
        private int page;
        private int pageSize;
        private long totalElements;
        private int totalPages;
        private boolean hasNext;
        private boolean hasPrevious;

        public PaginatedResponseBuilder<T> success(boolean success) {
            this.success = success;
            return this;
        }

        public PaginatedResponseBuilder<T> message(String message) {
            this.message = message;
            return this;
        }

        public PaginatedResponseBuilder<T> code(String code) {
            this.code = code;
            return this;
        }

        public PaginatedResponseBuilder<T> timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public PaginatedResponseBuilder<T> path(String path) {
            this.path = path;
            return this;
        }

        public PaginatedResponseBuilder<T> data(List<T> data) {
            this.data = data;
            return this;
        }

        public PaginatedResponseBuilder<T> page(int page) {
            this.page = page;
            return this;
        }

        public PaginatedResponseBuilder<T> pageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public PaginatedResponseBuilder<T> totalElements(long totalElements) {
            this.totalElements = totalElements;
            return this;
        }

        public PaginatedResponseBuilder<T> totalPages(int totalPages) {
            this.totalPages = totalPages;
            return this;
        }

        public PaginatedResponseBuilder<T> hasNext(boolean hasNext) {
            this.hasNext = hasNext;
            return this;
        }

        public PaginatedResponseBuilder<T> hasPrevious(boolean hasPrevious) {
            this.hasPrevious = hasPrevious;
            return this;
        }

        public PaginatedResponse<T> build() {
            return new PaginatedResponse<>(
                    success,
                    message,
                    code,
                    timestamp,
                    path,
                    data,
                    page,
                    pageSize,
                    totalElements,
                    totalPages,
                    hasNext,
                    hasPrevious
            );
        }
    }
}

