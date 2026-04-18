package com.foodorder.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.foodorder.entity.ShoppingCartItem;
import com.foodorder.entity.User;

public interface ShoppingCartItemRepository extends JpaRepository<ShoppingCartItem, Long> {
    @EntityGraph(attributePaths = {"menuItem"})
    List<ShoppingCartItem> findByUserOrderByIdAsc(User user);

    Optional<ShoppingCartItem> findByUserIdAndMenuItemId(Long userId, Long menuItemId);

    void deleteByUserIdAndMenuItemId(Long userId, Long menuItemId);

    void deleteByUserId(Long userId);
}
