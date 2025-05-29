package org.example.restaurantpos.controller;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import org.example.restaurantpos.entity.Category;
import org.example.restaurantpos.service.CategoryService;


@RestController
@RequestMapping("/api/categories")
@Tag(name = "Category Management")
public class CategoryController {
private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    @PostMapping
    @Operation(summary = "Add new category")
    public ResponseEntity<Category> addCategory(@RequestBody Category category) {
        Category saved = categoryService.addCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    @Operation(summary = "List categories")
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }
}
