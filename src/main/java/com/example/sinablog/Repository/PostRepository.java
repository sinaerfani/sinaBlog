package com.example.sinablog.Repository;

import com.example.sinablog.model.Post;
import com.example.sinablog.model.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByDisableDateIsNull(Pageable pageable);

    // ==================== پست‌های فعال ====================

    Optional<Post> findByIdAndDisableDateIsNull(Long id);
    Optional<Post> findBySlugAndDisableDateIsNull(String slug);

    // پست‌های بر اساس وضعیت
    List<Post> findByStatusAndDisableDateIsNull(PostStatus status);
    Page<Post> findByStatusAndDisableDateIsNull(PostStatus status, Pageable pageable);

    // پست‌های یک دسته‌بندی
    List<Post> findByCategoryIdAndDisableDateIsNull(Long categoryId);
    Page<Post> findByCategoryIdAndDisableDateIsNull(Long categoryId, Pageable pageable);
    List<Post> findByCategoryIdAndStatusAndDisableDateIsNull(Long categoryId, PostStatus status);

    // پست‌های یک نویسنده
    List<Post> findByAuthorIdAndDisableDateIsNull(Long authorId);
    Page<Post> findByAuthorIdAndDisableDateIsNull(Long authorId, Pageable pageable);
    List<Post> findByAuthorIdAndStatusAndDisableDateIsNull(Long authorId, PostStatus status);

    // پست‌های دارای تگ خاص
    List<Post> findByTagsIdAndDisableDateIsNull(Long tagId);
    List<Post> findByTagsIdAndStatusAndDisableDateIsNull(Long tagId, PostStatus status);

    // پست‌های جدیدترین اول
    List<Post> findByDisableDateIsNullOrderByCreatedAtDesc();
    List<Post> findByStatusAndDisableDateIsNullOrderByCreatedAtDesc(PostStatus status);
    List<Post> findByCategoryIdAndDisableDateIsNullOrderByCreatedAtDesc(Long categoryId);

    // ==================== پست‌های حذف شده ====================

    List<Post> findByDisableDateIsNotNull();
    Page<Post> findByDisableDateIsNotNull(Pageable pageable);
    List<Post> findByAuthorIdAndDisableDateIsNotNull(Long authorId);

    // ==================== شمارش‌ها ====================

    long countByDisableDateIsNull();
    long countByStatusAndDisableDateIsNull(PostStatus status);
    long countByCategoryIdAndDisableDateIsNull(Long categoryId);
    long countByAuthorIdAndDisableDateIsNull(Long authorId);
    long countByTagsIdAndDisableDateIsNull(Long tagId);

    long countByDisableDateIsNotNull();
    long countByAuthorIdAndDisableDateIsNotNull(Long authorId);

    // ==================== جستجو ====================

    @Query("SELECT p FROM Post p WHERE (LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND p.disableDate IS NULL")
    List<Post> searchInActivePosts(@Param("keyword") String keyword);

    @Query("SELECT p FROM Post p WHERE (LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND p.disableDate IS NULL")
    Page<Post> searchInActivePosts(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE (LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND p.status = 'PUBLISHED' AND p.disableDate IS NULL")
    List<Post> searchPublishedPosts(@Param("keyword") String keyword);

    @Query("SELECT p FROM Post p WHERE (LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND p.status = 'PUBLISHED' AND p.disableDate IS NULL")
    Page<Post> searchPublishedPosts(@Param("keyword") String keyword, Pageable pageable);

    // ==================== پست‌های منتشر شده جدید ====================

    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' AND p.disableDate IS NULL ORDER BY p.publishedAt DESC")
    List<Post> findLatestPublishedPosts(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' AND p.disableDate IS NULL AND p.category.id = :categoryId ORDER BY p.publishedAt DESC")
    List<Post> findLatestPublishedPostsByCategory(@Param("categoryId") Long categoryId, Pageable pageable);

    // ==================== بررسی وجود ====================

    boolean existsBySlugAndDisableDateIsNull(String slug);
    boolean existsByCategoryIdAndDisableDateIsNull(Long categoryId);
    boolean existsByAuthorIdAndDisableDateIsNull(Long authorId);
    boolean existsByTagsIdAndDisableDateIsNull(Long tagId);
}