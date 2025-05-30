package org.example.restaurantpos.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.example.restaurantpos.entity.Category;
import org.example.restaurantpos.repository.CategoryRepository;
import org.example.restaurantpos.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category addCategory(Category category) {
        String name = category.getCategoryName().trim();

        if (categoryRepository.existsByCategoryNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Category with name '" + name + "' already exists.");
        }

        category.setCategoryName(name);
        return categoryRepository.save(category);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + id));
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category not found with ID: " + id);
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public Category updateCategory(Long id, Category updated) {
        Category existing = getCategoryById(id);

        String newName = updated.getCategoryName().trim();
        if (!existing.getCategoryName().equalsIgnoreCase(newName)
                && categoryRepository.existsByCategoryNameIgnoreCase(newName)) {
            throw new IllegalArgumentException("Category with name '" + newName + "' already exists.");
        }

        existing.setCategoryName(newName);
        return categoryRepository.save(existing);
    }
}
