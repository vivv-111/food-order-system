package com.foodorder.dto;

public record AuthResponse(
    String token,
    String userId,
    String role
) {}
