package com.posportfolio.dto;

import com.posportfolio.model.Role;

public record UserDto(Long id, String username, Role role) {
}
