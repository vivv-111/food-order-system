package com.foodorder.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.foodorder.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @EntityGraph(attributePaths = {"menuItem"})
    List<OrderItem> findByOrderIdOrderByIdAsc(Long orderId);

    Optional<OrderItem> findByOrderIdAndMenuItemId(Long orderId, Long menuItemId);

    void deleteByOrderId(Long orderId);
}
