package com.foodorder.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foodorder.dto.OrderItemResponse;
import com.foodorder.dto.OrderResponse;
import com.foodorder.entity.Order;
import com.foodorder.entity.OrderItem;
import com.foodorder.entity.ShoppingCartItem;
import com.foodorder.entity.User;
import com.foodorder.exception.BusinessException;
import com.foodorder.repository.OrderItemRepository;
import com.foodorder.repository.OrderRepository;
import com.foodorder.repository.ShoppingCartItemRepository;

@Service
public class OrderService {

    private final CurrentUserService currentUserService;
    private final ShoppingCartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService(
        CurrentUserService currentUserService,
        ShoppingCartItemRepository cartItemRepository,
        OrderRepository orderRepository,
        OrderItemRepository orderItemRepository
    ) {
        this.currentUserService = currentUserService;
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional
    public OrderResponse submitCurrentCart() {
        User user = currentUserService.requireCurrentUser();
        List<ShoppingCartItem> cartItems = cartItemRepository.findByUserOrderByIdAsc(user);

        if (cartItems.isEmpty()) {
            throw new BusinessException("Shopping cart is empty");
        }

        BigDecimal totalAmount = cartItems.stream()
            .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        Order savedOrder = orderRepository
            .findFirstByUserIdAndCreatedAtGreaterThanEqualOrderByCreatedAtDesc(user.getId(), oneHourAgo)
            .orElseGet(() -> {
                Order order = new Order();
                order.setUser(user);
                order.setStatus("PENDING");
                order.setTotalAmount(BigDecimal.ZERO);
                return orderRepository.save(order);
            });

        for (ShoppingCartItem cartItem : cartItems) {
            BigDecimal lineTotal = cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderItem orderItem = orderItemRepository
                .findByOrderIdAndMenuItemId(savedOrder.getId(), cartItem.getMenuItem().getId())
                .orElseGet(() -> {
                    OrderItem item = new OrderItem();
                    item.setOrder(savedOrder);
                    item.setMenuItem(cartItem.getMenuItem());
                    item.setItemName(cartItem.getMenuItem().getName());
                    item.setQuantity(0);
                    item.setUnitPrice(cartItem.getUnitPrice());
                    item.setLineTotal(BigDecimal.ZERO);
                    return item;
                });

            orderItem.setQuantity(orderItem.getQuantity() + cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getUnitPrice());
            orderItem.setLineTotal(orderItem.getLineTotal().add(lineTotal));
            orderItemRepository.save(orderItem);
        }

        savedOrder.setTotalAmount(savedOrder.getTotalAmount().add(totalAmount));
        orderRepository.save(savedOrder);

        cartItemRepository.deleteByUserId(user.getId());

        return toOrderResponse(savedOrder);
    }

    public List<OrderResponse> getCurrentUserOrders() {
        User user = currentUserService.requireCurrentUser();
        List<Order> orders = orderRepository.findByUserIdOrderByIdDesc(user.getId());

        return orders.stream().map(this::toOrderResponse).toList();
    }

    public List<OrderResponse> getAllOrdersForAdmin() {
        List<Order> orders = orderRepository.findAllByOrderByIdDesc();

        return orders.stream().map(this::toOrderResponse).toList();
    }

    @Transactional
    public void deleteOrderByIdForAdmin(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new BusinessException("Order not found");
        }

        orderItemRepository.deleteByOrderId(orderId);
        orderRepository.deleteById(orderId);
    }

    private OrderResponse toOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = orderItemRepository.findByOrderIdOrderByIdAsc(order.getId())
            .stream()
            .map(item -> new OrderItemResponse(
                item.getMenuItem().getId(),
                item.getItemName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getLineTotal()
            ))
            .toList();

        return new OrderResponse(
            order.getId(),
            order.getUser().getUserId(),
            order.getUser().getUserName(),
            order.getStatus(),
            order.getTotalAmount(),
            order.getCreatedAt(),
            itemResponses
        );
    }
}
