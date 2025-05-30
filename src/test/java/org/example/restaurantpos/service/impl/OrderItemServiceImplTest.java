package org.example.restaurantpos.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.example.restaurantpos.entity.MenuItem;
import org.example.restaurantpos.entity.Order;
import org.example.restaurantpos.entity.OrderItem;
import org.example.restaurantpos.repository.MenuItemRepository;
import org.example.restaurantpos.repository.OrderItemRepository;
import org.example.restaurantpos.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderItemServiceImplTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @InjectMocks
    private OrderItemServiceImpl orderItemService;

    private Order order;
    private MenuItem menuItem;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        order = new Order();
        order.setId(1L);

        menuItem = new MenuItem();
        menuItem.setId(2L);
        menuItem.setItemName("Pizza");
        menuItem.setPrice(BigDecimal.valueOf(12.99));

        orderItem = new OrderItem();
        orderItem.setId(10L);
        orderItem.setQuantity(2);
        orderItem.setItem(menuItem);
    }

    @Test
    void shouldAddItemToOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(menuItemRepository.findById(2L)).thenReturn(Optional.of(menuItem));
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderItem result = orderItemService.addItemToOrder(1L, orderItem);

        assertEquals(order, result.getOrder());
        assertEquals(menuItem, result.getItem());
        assertEquals(menuItem.getPrice(), result.getItemPrice());
        assertEquals(2, result.getQuantity());
    }

    @Test
    void shouldThrowWhenOrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderItemService.addItemToOrder(1L, orderItem));
    }

    @Test
    void shouldThrowWhenMenuItemNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(menuItemRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderItemService.addItemToOrder(1L, orderItem));
    }

    @Test
    void shouldUpdateOrderItemQuantity() {
        OrderItem updated = new OrderItem();
        updated.setQuantity(5);

        when(orderItemRepository.findById(10L)).thenReturn(Optional.of(orderItem));
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderItem result = orderItemService.updateOrderItem(10L, updated);

        assertEquals(5, result.getQuantity());
    }

    @Test
    void shouldThrowWhenOrderItemNotFoundForUpdate() {
        when(orderItemRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderItemService.updateOrderItem(10L, orderItem));
    }

    @Test
    void shouldRemoveOrderItem() {
        when(orderItemRepository.existsById(10L)).thenReturn(true);

        orderItemService.removeOrderItem(10L);

        verify(orderItemRepository).deleteById(10L);
    }

    @Test
    void shouldThrowWhenRemovingNonexistentOrderItem() {
        when(orderItemRepository.existsById(10L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> orderItemService.removeOrderItem(10L));
    }
}
