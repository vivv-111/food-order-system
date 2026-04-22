package com.foodorder.dto;

import java.math.BigDecimal;

public record CartItemResponse(
    Long menuItemId,
    String name,
    Integer quantity,
    BigDecimal unitPrice,
    BigDecimal lineTotal
) {}
