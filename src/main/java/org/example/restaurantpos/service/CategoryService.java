package org.example.restaurantpos.service;

 import java.util.List;
 import org.example.restaurantpos.entity.Category;

public interface CategoryService {
    Category addCategory(Category category);
    List<Category> getAllCategories();
    Category getCategoryById(Long id);
    Category updateCategory(Long id, Category category);
    void deleteCategory(Long id);
}
