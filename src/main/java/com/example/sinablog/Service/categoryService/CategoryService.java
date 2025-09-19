package com.example.sinablog.Service.categoryService;

import com.example.sinablog.model.Category;

import java.util.List;

public interface CategoryService {
        Category createCategory(Category category);
        Category updateCategory(Long id, Category category);
        void deleteCategory(Long id);
        Category getCategoryById(Long id);
        Category getCategoryBySlug(String slug);
        List<Category> getAllCategories();
        List<Category> getPopularCategories(int limit);
        boolean existsByName(String name);
        boolean existsBySlug(String slug);

}
