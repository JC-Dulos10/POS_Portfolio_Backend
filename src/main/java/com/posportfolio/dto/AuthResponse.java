package com.posportfolio.dto;

public record AuthResponse(String accessToken, String tokenType, String role) {
}
