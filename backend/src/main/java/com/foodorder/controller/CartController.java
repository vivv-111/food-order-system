package com.foodorder.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodorder.dto.CartResponse;
import com.foodorder.dto.CartUpsertRequest;
import com.foodorder.service.CartService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
@Validated
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public CartResponse getCart() {
        return cartService.getCurrentUserCart();
    }

    @PostMapping("/items")
    public CartResponse upsertItem(@Valid @RequestBody CartUpsertRequest request) {
        return cartService.upsertItem(request);
    }

    @DeleteMapping("/items/{menuItemId}")
    public CartResponse removeItem(@PathVariable Long menuItemId) {
        return cartService.removeItem(menuItemId);
    }
}
