package org.example.restaurantpos.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.example.restaurantpos.entity.Category;
import org.example.restaurantpos.entity.MenuItem;
import org.example.restaurantpos.repository.CategoryRepository;
import org.example.restaurantpos.repository.MenuItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MenuItemServiceImplTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private MenuItemServiceImpl menuItemService;

    private MenuItem menuItem;
    private Category category;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        category = new Category();
        category.setId(1L);
        category.setCategoryName("FOOD");

        menuItem = new MenuItem();
        menuItem.setId(10L);
        menuItem.setItemName("Burger");
        menuItem.setPrice(BigDecimal.valueOf(9.99));
        menuItem.setAvailable(true);
        menuItem.setCategory(category);
    }

    @Test
    void shouldAddMenuItemWithValidCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(menuItemRepository.save(menuItem)).thenReturn(menuItem);

        MenuItem result = menuItemService.addMenuItem(menuItem);

        assertEquals("Burger", result.getItemName());
        assertEquals(category, result.getCategory());
    }

    @Test
    void shouldThrowWhenCategoryInvalid() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        menuItem.setCategory(category);
        assertThrows(IllegalArgumentException.class, () -> menuItemService.addMenuItem(menuItem));
    }

    @Test
    void shouldGetAllMenuItems() {
        when(menuItemRepository.findAll()).thenReturn(List.of(menuItem));
        List<MenuItem> result = menuItemService.getAllItems();
        assertEquals(1, result.size());
        assertEquals("Burger", result.get(0).getItemName());
    }

    @Test
    void shouldGetItemById() {
        when(menuItemRepository.findById(10L)).thenReturn(Optional.of(menuItem));
        MenuItem result = menuItemService.getItemById(10L);
        assertEquals("Burger", result.getItemName());
    }

    @Test
    void shouldThrowWhenItemNotFoundById() {
        when(menuItemRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> menuItemService.getItemById(10L));
    }

    @Test
    void shouldUpdateItem() {
        MenuItem updated = new MenuItem();
        updated.setItemName("Updated Burger");
        updated.setDescription("New Description");
        updated.setPrice(BigDecimal.valueOf(12.00));
        updated.setAvailable(false);
        updated.setCategory(category);

        when(menuItemRepository.findById(10L)).thenReturn(Optional.of(menuItem));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(menuItemRepository.save(any(MenuItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MenuItem result = menuItemService.updateItem(10L, updated);

        assertEquals("Updated Burger", result.getItemName());
        assertEquals("New Description", result.getDescription());
        assertEquals(BigDecimal.valueOf(12.00), result.getPrice());
        assertFalse(result.isAvailable());
        assertEquals(category, result.getCategory());
    }

    @Test
    void shouldToggleAvailability() {
        when(menuItemRepository.findById(10L)).thenReturn(Optional.of(menuItem));
        menuItemService.toggleAvailability(10L);
        assertFalse(menuItem.isAvailable());
        verify(menuItemRepository).save(menuItem);
    }

    @Test
    void shouldDeleteItem() {
        menuItemService.deleteItem(10L);
        verify(menuItemRepository).deleteById(10L);
    }
}
