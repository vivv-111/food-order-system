package com.foodorder.dto;

import java.math.BigDecimal;

public record MenuItemResponse(
    Long id,
    String name,
    String type,
    String ingredients,
    String description,
    BigDecimal price,
    String imageUrl
) {}
