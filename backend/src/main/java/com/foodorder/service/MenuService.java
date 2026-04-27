package com.foodorder.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.foodorder.dto.MenuItemRequest;
import com.foodorder.dto.MenuItemResponse;
import com.foodorder.entity.MenuItem;
import com.foodorder.repository.MenuItemRepository;

@Service
public class MenuService {

    private final MenuItemRepository menuItemRepository;

    public MenuService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public List<MenuItemResponse> getMenu(String type) {
        List<MenuItem> items = (type == null || type.isBlank())
            ? menuItemRepository.findAll()
            : menuItemRepository.findByTypeOrderByNameAsc(type);

        return items.stream().map(this::toResponse).toList();
    }

    public MenuItemResponse createMenuItem(MenuItemRequest request) {
        MenuItem item = new MenuItem();
        item.setName(request.name());
        item.setType(request.type());
        item.setIngredients(request.ingredients());
        item.setDescription(request.description());
        item.setPrice(request.price());
        item.setImageUrl(request.imageUrl());

        return toResponse(menuItemRepository.save(item));
    }

    private MenuItemResponse toResponse(MenuItem item) {
        return new MenuItemResponse(
            item.getId(),
            item.getName(),
            item.getType(),
            item.getIngredients(),
            item.getDescription(),
            item.getPrice(),
            item.getImageUrl()
        );
    }
}
