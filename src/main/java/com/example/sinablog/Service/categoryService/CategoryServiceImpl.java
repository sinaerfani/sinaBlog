package com.example.sinablog.Service.categoryService;

import com.example.sinablog.model.Category;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CategoryServiceImpl implements CategoryService {
    @Override
    public Category createCategory(Category category) {
        return null;
    }

    @Override
    public Category updateCategory(Long id, Category category) {
        return null;
    }

    @Override
    public void deleteCategory(Long id) {

    }

    @Override
    public Category getCategoryById(Long id) {
        return null;
    }

    @Override
    public Category getCategoryBySlug(String slug) {
        return null;
    }

    @Override
    public List<Category> getAllCategories() {
        return null;
    }

    @Override
    public List<Category> getPopularCategories(int limit) {
        return null;
    }

    @Override
    public boolean existsByName(String name) {
        return false;
    }

    @Override
    public boolean existsBySlug(String slug) {
        return false;
    }
}
