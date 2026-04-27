package com.foodorder.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodorder.dto.MenuItemRequest;
import com.foodorder.dto.MenuItemResponse;
import com.foodorder.service.MenuService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/menu")
@Validated
public class AdminMenuController {

    private final MenuService menuService;

    public AdminMenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @PostMapping
    public MenuItemResponse create(@Valid @RequestBody MenuItemRequest request) {
        return menuService.createMenuItem(request);
    }
}
