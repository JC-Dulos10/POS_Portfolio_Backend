package com.posportfolio.dto;

import com.posportfolio.model.Role;

public record SignupRequest(String username, String password, Role role) {
}
