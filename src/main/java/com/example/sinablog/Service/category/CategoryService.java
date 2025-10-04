package com.example.sinablog.Service.category;


import com.example.sinablog.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    // ==================== عملیات CRUD ====================
    Category createCategory(Category category);

    Category updateCategory(Long id, Category category);

    void deleteCategory(Long id); // Soft Delete

    Optional<Category> getCategoryById(Long id);

    Optional<Category> getCategoryByName(String name);

    Optional<Category> getCategoryBySlug(String slug);

    // ==================== دسته‌بندی‌های فعال ====================
    List<Category> getAllCategories();

    Page<Category> getAllCategories(Pageable pageable);

    List<Category> searchCategories(String keyword);

    // ==================== دسته‌بندی‌های حذف شده ====================
    List<Category> getDeletedCategories();

    void restoreCategory(Long id);

    void permanentDeleteCategory(Long id); // حذف فیزیکی

    // ==================== شمارش‌ها ====================
    long countAllCategories();

    long countDeletedCategories();

    // ==================== بررسی وجود ====================
    boolean existsByName(String name);

    boolean existsBySlug(String slug);

    boolean existsByNameAndIdNot(String name, Long id);

    boolean existsBySlugAndIdNot(String slug, Long id);



}