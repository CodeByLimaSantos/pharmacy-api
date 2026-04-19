package com.limasantos.pharmacy.api.user.model;

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
