package com.foodorder.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodorder.dto.OrderResponse;
import com.foodorder.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/submit")
    public OrderResponse submitCurrentCart() {
        return orderService.submitCurrentCart();
    }

    @GetMapping
    public List<OrderResponse> getCurrentUserOrders() {
        return orderService.getCurrentUserOrders();
    }
}
