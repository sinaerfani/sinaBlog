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

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // یافتن دسته‌بندی فعال بر اساس ID
    Optional<Category> findByIdAndDisableDateIsNull(Long id);

    // یافتن دسته‌بندی بر اساس نام
    Optional<Category> findByNameAndDisableDateIsNull(String name);

    // یافتن دسته‌بندی بر اساس slug
    Optional<Category> findBySlugAndDisableDateIsNull(String slug);

    // بررسی وجود دسته‌بندی بر اساس نام
    boolean existsByNameAndDisableDateIsNull(String name);

    // بررسی وجود دسته‌بندی بر اساس slug
    boolean existsBySlugAndDisableDateIsNull(String slug);

    // دریافت همه دسته‌بندی‌های فعال
    List<Category> findAllByDisableDateIsNull();

    // دریافت همه دسته‌بندی‌های فعال با صفحه‌بندی
    Page<Category> findAllByDisableDateIsNull(Pageable pageable);

    // جستجوی دسته‌بندی‌ها بر اساس نام
    List<Category> findByNameContainingIgnoreCaseAndDisableDateIsNull(String name);

    // جستجوی دسته‌بندی‌ها بر اساس slug
    List<Category> findBySlugContainingAndDisableDateIsNull(String slug);

    // دریافت دسته‌بندی‌های حذف شده
    List<Category> findByDisableDateIsNotNull();

    // شمارش دسته‌بندی‌های فعال
    long countByDisableDateIsNull();

    // شمارش دسته‌بندی‌های حذف شده
    long countByDisableDateIsNotNull();

}