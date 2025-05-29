package org.example.restaurantpos.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.example.restaurantpos.entity.Category;
import org.example.restaurantpos.entity.MenuItem;
import org.example.restaurantpos.repository.CategoryRepository;
import org.example.restaurantpos.repository.MenuItemRepository;
import org.example.restaurantpos.service.MenuItemService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final CategoryRepository categoryRepository;

    public MenuItemServiceImpl(MenuItemRepository menuItemRepository, CategoryRepository categoryRepository) {
        this.menuItemRepository = menuItemRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public MenuItem addMenuItem(MenuItem item) {
        validateCategory(item);
        return menuItemRepository.save(item);
    }

    @Override
    public List<MenuItem> getAllItems() {
        return menuItemRepository.findAll();
    }

    @Override
    public MenuItem getItemById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Menu item not found with ID: " + id));
    }

    @Override
    public MenuItem updateItem(Long id, MenuItem updatedItem) {
        MenuItem existing = getItemById(id);
        existing.setItemName(updatedItem.getItemName());
        existing.setDescription(updatedItem.getDescription());
        existing.setPrice(updatedItem.getPrice());
        existing.setAvailable(updatedItem.isAvailable());
        validateCategory(updatedItem);
        existing.setCategory(updatedItem.getCategory());
        return menuItemRepository.save(existing);
    }

    @Override
    public void toggleAvailability(Long id) {
        MenuItem item = getItemById(id);
        item.setAvailable(!item.isAvailable());
        menuItemRepository.save(item);
    }

    @Override
    public void deleteItem(Long id) {
        menuItemRepository.deleteById(id);
    }

    private void validateCategory(MenuItem item) {
        if (item.getCategory() != null && item.getCategory().getId() != null) {
            Category category = categoryRepository.findById(item.getCategory().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));
            item.setCategory(category);
        }
    }
}
