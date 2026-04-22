package com.foodorder.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartUpsertRequest(
    @NotNull Long menuItemId,
    @NotNull @Min(0) Integer quantity
) {}
