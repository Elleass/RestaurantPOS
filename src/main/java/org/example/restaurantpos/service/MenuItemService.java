package org.example.restaurantpos.service;

import org.example.restaurantpos.entity.MenuItem;

import java.util.List;

public interface MenuItemService {
    MenuItem addMenuItem(MenuItem item);
    List<MenuItem> getAllItems();
    MenuItem getItemById(Long id);
    MenuItem updateItem(Long id, MenuItem item);
    void toggleAvailability(Long id);
    void deleteItem(Long id);
}
