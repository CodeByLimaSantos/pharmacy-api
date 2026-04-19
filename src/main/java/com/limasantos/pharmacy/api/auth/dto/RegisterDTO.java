package com.limasantos.pharmacy.api.auth.dto;

import com.limasantos.pharmacy.api.user.models.UserRole;

public record RegisterDTO(String username, String password, String email, UserRole role) {
}
