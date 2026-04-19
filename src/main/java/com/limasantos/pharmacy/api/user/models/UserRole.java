package com.limasantos.pharmacy.api.user.models;

public enum UserRole {
        ROLE_ADMIN("ADMIN"),
        ROLE_CAIXA("CAIXA");

        private final String Role;

        UserRole(String role) {
                Role = role;
        }

        public String getRole() {
                return Role;
        }

    }
