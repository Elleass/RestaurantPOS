package org.example.restaurantpos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.restaurantpos.entity.MenuItem;
import org.example.restaurantpos.service.MenuItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu-items")
@Tag(name = "Menu Item Management")
public class MenuItemController {

    private final MenuItemService menuItemService;

    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Add menu item (ADMIN only)")
    public ResponseEntity<MenuItem> addMenuItem(@RequestBody MenuItem item) {
        return ResponseEntity.status(HttpStatus.CREATED).body(menuItemService.addMenuItem(item));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    @Operation(summary = "List menu items (USER, ADMIN)")
    public ResponseEntity<List<MenuItem>> getAllItems() {
        return ResponseEntity.ok(menuItemService.getAllItems());
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Get item by ID (USER, ADMIN)")
    public ResponseEntity<MenuItem> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(menuItemService.getItemById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update item (ADMIN only)")
    public ResponseEntity<MenuItem> updateItem(@PathVariable Long id, @RequestBody MenuItem item) {
        return ResponseEntity.ok(menuItemService.updateItem(id, item));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/availability")
    @Operation(summary = "Toggle availability (ADMIN only)")
    public ResponseEntity<Void> toggleAvailability(@PathVariable Long id) {
        menuItemService.toggleAvailability(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete item (ADMIN only)")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        menuItemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
