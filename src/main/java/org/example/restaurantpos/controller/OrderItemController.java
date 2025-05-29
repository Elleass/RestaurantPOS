package org.example.restaurantpos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.restaurantpos.entity.OrderItem;
import org.example.restaurantpos.service.OrderItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "Order Item Management")
public class OrderItemController {

    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @PostMapping("/orders/{orderId}/items")
    @Operation(summary = "Add item to order")
    public ResponseEntity<OrderItem> addItemToOrder(@PathVariable Long orderId, @RequestBody OrderItem orderItem) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderItemService.addItemToOrder(orderId, orderItem));
    }

    @PutMapping("/order-items/{id}")
    @Operation(summary = "Update quantity")
    public ResponseEntity<OrderItem> updateOrderItem(@PathVariable Long id, @RequestBody OrderItem updatedItem) {
        return ResponseEntity.ok(orderItemService.updateOrderItem(id, updatedItem));
    }

    @DeleteMapping("/order-items/{id}")
    @Operation(summary = "Remove item from order")
    public ResponseEntity<Void> removeOrderItem(@PathVariable Long id) {
        orderItemService.removeOrderItem(id);
        return ResponseEntity.noContent().build();
    }
}
