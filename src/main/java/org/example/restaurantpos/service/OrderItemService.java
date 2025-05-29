package org.example.restaurantpos.service;

import org.example.restaurantpos.entity.OrderItem;

public interface OrderItemService {
    OrderItem addItemToOrder(Long orderId, OrderItem orderItem);
    OrderItem updateOrderItem(Long id, OrderItem orderItem);
    void removeOrderItem(Long id);
}
