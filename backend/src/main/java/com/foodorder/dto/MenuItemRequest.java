package com.foodorder.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MenuItemRequest(
    @NotBlank String name,
    @NotBlank String type,
    String ingredients,
    String description,
    @NotNull @DecimalMin(value = "0.01") BigDecimal price,
    String imageUrl
) {}
