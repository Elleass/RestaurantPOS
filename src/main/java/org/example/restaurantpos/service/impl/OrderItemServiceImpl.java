package org.example.restaurantpos.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.example.restaurantpos.entity.MenuItem;
import org.example.restaurantpos.entity.Order;
import org.example.restaurantpos.entity.OrderItem;
import org.example.restaurantpos.repository.MenuItemRepository;
import org.example.restaurantpos.repository.OrderItemRepository;
import org.example.restaurantpos.repository.OrderRepository;
import org.example.restaurantpos.service.OrderItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;

    public OrderItemServiceImpl(OrderItemRepository orderItemRepository,
                                OrderRepository orderRepository,
                                MenuItemRepository menuItemRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
    }

    @Override
    public OrderItem addItemToOrder(Long orderId, OrderItem orderItem) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        Long itemId = orderItem.getItem().getId();
        MenuItem menuItem = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Menu item not found"));

        orderItem.setOrder(order);
        orderItem.setItem(menuItem);
        orderItem.setItemPrice(menuItem.getPrice());

        return orderItemRepository.save(orderItem);
    }

    @Override
    public OrderItem updateOrderItem(Long id, OrderItem updatedItem) {
        OrderItem existing = orderItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order item not found"));

        existing.setQuantity(updatedItem.getQuantity());

        return orderItemRepository.save(existing);
    }

    @Override
    public void removeOrderItem(Long id) {
        if (!orderItemRepository.existsById(id)) {
            throw new EntityNotFoundException("Order item not found");
        }
        orderItemRepository.deleteById(id);
    }
}
