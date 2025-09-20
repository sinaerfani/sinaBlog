package com.example.sinablog.Repository;

import com.example.sinablog.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface CategoryRepository extends JpaRepository<Category, Long> {

    // ==================== دسته‌بندی‌های فعال ====================

    Optional<Category> findByIdAndDisableDateIsNull(Long id);
    Optional<Category> findByNameAndDisableDateIsNull(String name);
    Optional<Category> findBySlugAndDisableDateIsNull(String slug);

    List<Category> findByDisableDateIsNull();
    Page<Category> findByDisableDateIsNull(Pageable pageable);

    List<Category> findByNameContainingAndDisableDateIsNull(String name);
    List<Category> findBySlugContainingAndDisableDateIsNull(String slug);
    List<Category> findByDescriptionContainingAndDisableDateIsNull(String description);

    List<Category> findByDisableDateIsNullOrderByNameAsc();
    List<Category> findByDisableDateIsNullOrderByCreatedAtDesc();

    // ==================== دسته‌بندی‌های حذف شده ====================

    List<Category> findByDisableDateIsNotNull();
    Page<Category> findByDisableDateIsNotNull(Pageable pageable);

    // ==================== شمارش‌ها ====================

    long countByDisableDateIsNull();
    long countByDisableDateIsNotNull();

    // ==================== بررسی وجود ====================

    boolean existsByNameAndDisableDateIsNull(String name);
    boolean existsBySlugAndDisableDateIsNull(String slug);
    boolean existsByNameAndIdNotAndDisableDateIsNull(String name, Long id);
    boolean existsBySlugAndIdNotAndDisableDateIsNull(String slug, Long id);

    // ==================== جستجوی پیشرفته ====================

    @Query("SELECT c FROM Category c WHERE " +
            "(LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.slug) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "c.disableDate IS NULL")
    List<Category> searchActiveCategories(@Param("keyword") String keyword);
}