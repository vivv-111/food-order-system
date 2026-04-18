package com.foodorder.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.foodorder.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = {"user"})
    List<Order> findByUserIdOrderByIdDesc(Long userId);

    @EntityGraph(attributePaths = {"user"})
    Optional<Order> findFirstByUserIdAndCreatedAtGreaterThanEqualOrderByCreatedAtDesc(Long userId, LocalDateTime createdAt);

    @EntityGraph(attributePaths = {"user"})
    List<Order> findAllByOrderByIdDesc();
}
