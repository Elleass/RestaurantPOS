package org.example.restaurantpos.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.example.restaurantpos.entity.Order;
import org.example.restaurantpos.entity.OrderItem;
import org.example.restaurantpos.repository.OrderRepository;
import org.example.restaurantpos.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order createOrder(Order order) {
        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {
                item.setOrder(order);
            }
        }
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + id));
    }

    @Override
    public Order updateOrder(Long id, Order updatedOrder) {
        Order existingOrder = getOrderById(id);
        existingOrder.setStatus(updatedOrder.getStatus());
        return orderRepository.save(existingOrder);
    }

    @Override
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new EntityNotFoundException("Order not found with ID: " + id);
        }
        orderRepository.deleteById(id);
    }
}
