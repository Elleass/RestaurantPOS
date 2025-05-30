package org.example.restaurantpos.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.example.restaurantpos.entity.Category;
import org.example.restaurantpos.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        category = new Category();
        category.setId(1L);
        category.setCategoryName("Desserts");
    }

    @Test
    void shouldAddCategorySuccessfully() {
        when(categoryRepository.existsByCategoryNameIgnoreCase("Desserts")).thenReturn(false);
        when(categoryRepository.save(category)).thenReturn(category);

        Category result = categoryService.addCategory(category);

        assertEquals("Desserts", result.getCategoryName());
        verify(categoryRepository).save(category);
    }

    @Test
    void shouldTrimAndRejectDuplicateCategoryName() {
        category.setCategoryName("  Desserts  ");
        when(categoryRepository.existsByCategoryNameIgnoreCase("Desserts")).thenReturn(true);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> categoryService.addCategory(category));
        assertTrue(ex.getMessage().contains("already exists"));
    }

    @Test
    void shouldReturnAllCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of(category));

        List<Category> result = categoryService.getAllCategories();

        assertEquals(1, result.size());
        assertEquals("Desserts", result.get(0).getCategoryName());
    }

    @Test
    void shouldGetCategoryById() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Category result = categoryService.getCategoryById(1L);

        assertEquals("Desserts", result.getCategoryName());
    }

    @Test
    void shouldThrowWhenCategoryNotFoundById() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.getCategoryById(1L));
    }

    @Test
    void shouldDeleteCategory() {
        when(categoryRepository.existsById(1L)).thenReturn(true);

        categoryService.deleteCategory(1L);

        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void shouldThrowWhenDeletingNonexistentCategory() {
        when(categoryRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> categoryService.deleteCategory(1L));
    }

    @Test
    void shouldUpdateCategoryName() {
        Category updated = new Category();
        updated.setCategoryName("Snacks");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByCategoryNameIgnoreCase("Snacks")).thenReturn(false);
        when(categoryRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Category result = categoryService.updateCategory(1L, updated);

        assertEquals("Snacks", result.getCategoryName());
    }

    @Test
    void shouldRejectDuplicateNameOnUpdate() {
        Category updated = new Category();
        updated.setCategoryName("Beverages");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByCategoryNameIgnoreCase("Beverages")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> categoryService.updateCategory(1L, updated));
    }
}
