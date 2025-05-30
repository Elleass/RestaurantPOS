package org.example.restaurantpos.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.example.restaurantpos.entity.Order;
import org.example.restaurantpos.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        order = new Order();
        order.setId(1L);
        order.setStatus("PENDING");
    }

    @Test
    void shouldCreateOrder() {
        when(orderRepository.save(order)).thenReturn(order);

        Order created = orderService.createOrder(order);

        assertEquals("PENDING", created.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void shouldGetAllOrders() {
        when(orderRepository.findAll()).thenReturn(List.of(order));

        List<Order> result = orderService.getAllOrders();

        assertEquals(1, result.size());
        assertEquals("PENDING", result.get(0).getStatus());
    }

    @Test
    void shouldGetOrderById() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order found = orderService.getOrderById(1L);

        assertEquals("PENDING", found.getStatus());
    }

    @Test
    void shouldThrowWhenOrderNotFoundById() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.getOrderById(1L));
    }

    @Test
    void shouldUpdateOrderStatus() {
        Order updated = new Order();
        updated.setStatus("COMPLETED");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.updateOrder(1L, updated);

        assertEquals("COMPLETED", result.getStatus());
    }

    @Test
    void shouldDeleteOrder() {
        when(orderRepository.existsById(1L)).thenReturn(true);

        orderService.deleteOrder(1L);

        verify(orderRepository).deleteById(1L);
    }

    @Test
    void shouldThrowWhenDeletingNonexistentOrder() {
        when(orderRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> orderService.deleteOrder(1L));
    }
}
