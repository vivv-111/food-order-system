package com.foodorder.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foodorder.dto.CartItemResponse;
import com.foodorder.dto.CartResponse;
import com.foodorder.dto.CartUpsertRequest;
import com.foodorder.entity.MenuItem;
import com.foodorder.entity.ShoppingCartItem;
import com.foodorder.entity.User;
import com.foodorder.exception.BusinessException;
import com.foodorder.repository.MenuItemRepository;
import com.foodorder.repository.ShoppingCartItemRepository;

@Service
public class CartService {

    private final ShoppingCartItemRepository cartItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final CurrentUserService currentUserService;

    public CartService(
        ShoppingCartItemRepository cartItemRepository,
        MenuItemRepository menuItemRepository,
        CurrentUserService currentUserService
    ) {
        this.cartItemRepository = cartItemRepository;
        this.menuItemRepository = menuItemRepository;
        this.currentUserService = currentUserService;
    }

    public CartResponse getCurrentUserCart() {
        User user = currentUserService.requireCurrentUser();
        List<ShoppingCartItem> items = cartItemRepository.findByUserOrderByIdAsc(user);
        return toResponse(items);
    }

    @Transactional
    public CartResponse upsertItem(CartUpsertRequest request) {
        User user = currentUserService.requireCurrentUser();

        MenuItem menuItem = menuItemRepository.findById(request.menuItemId())
            .orElseThrow(() -> new BusinessException("Menu item not found"));

        cartItemRepository.findByUserIdAndMenuItemId(user.getId(), menuItem.getId())
            .ifPresentOrElse(existing -> {
                if (request.quantity() == 0) {
                    cartItemRepository.delete(existing);
                } else {
                    existing.setQuantity(request.quantity());
                    existing.setUnitPrice(menuItem.getPrice());
                    cartItemRepository.save(existing);
                }
            }, () -> {
                if (request.quantity() > 0) {
                    ShoppingCartItem newItem = new ShoppingCartItem();
                    newItem.setUser(user);
                    newItem.setMenuItem(menuItem);
                    newItem.setQuantity(request.quantity());
                    newItem.setUnitPrice(menuItem.getPrice());
                    cartItemRepository.save(newItem);
                }
            });

        return getCurrentUserCart();
    }

    @Transactional
    public CartResponse removeItem(Long menuItemId) {
        User user = currentUserService.requireCurrentUser();
        cartItemRepository.deleteByUserIdAndMenuItemId(user.getId(), menuItemId);
        return getCurrentUserCart();
    }

    private CartResponse toResponse(List<ShoppingCartItem> items) {
        List<CartItemResponse> itemResponses = items.stream().map(item -> {
            BigDecimal lineTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            return new CartItemResponse(
                item.getMenuItem().getId(),
                item.getMenuItem().getName(),
                item.getQuantity(),
                item.getUnitPrice(),
                lineTotal
            );
        }).toList();

        BigDecimal total = itemResponses.stream()
            .map(CartItemResponse::lineTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(itemResponses, total);
    }
}
