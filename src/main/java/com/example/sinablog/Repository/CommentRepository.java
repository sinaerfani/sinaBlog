package com.example.sinablog.Repository;

import com.example.sinablog.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // ==================== کامنت‌های فعال ====================

    // یافتن کامنت فعال بر اساس ID
    Optional<Comment> findByIdAndDisableDateIsNull(Long id);

    // یافتن کامنت‌های فعال یک پست
    List<Comment> findByPostIdAndDisableDateIsNull(Long postId);
    Page<Comment> findByPostIdAndDisableDateIsNull(Long postId, Pageable pageable);

    // یافتن کامنت‌های تایید شده فعال
    List<Comment> findByApprovedTrueAndDisableDateIsNull();
    List<Comment> findByPostIdAndApprovedTrueAndDisableDateIsNull(Long postId);
    Page<Comment> findByPostIdAndApprovedTrueAndDisableDateIsNull(Long postId, Pageable pageable);

    // یافتن کامنت‌های در انتظار تایید فعال
    List<Comment> findByApprovedFalseAndDisableDateIsNull();
    List<Comment> findByPostIdAndApprovedFalseAndDisableDateIsNull(Long postId);
    Page<Comment> findByPostIdAndApprovedFalseAndDisableDateIsNull(Long postId, Pageable pageable);

    // یافتن کامنت‌های یک نویسنده فعال
    List<Comment> findByAuthorEmailAndDisableDateIsNull(String authorEmail);
    List<Comment> findByPostIdAndAuthorEmailAndDisableDateIsNull(Long postId, String authorEmail);

    // یافتن کامنت‌های جدیدترین اول (فعال)
    List<Comment> findByDisableDateIsNullOrderByCreatedAtDesc();
    List<Comment> findByPostIdAndDisableDateIsNullOrderByCreatedAtDesc(Long postId);
    List<Comment> findByApprovedTrueAndDisableDateIsNullOrderByCreatedAtDesc();
    List<Comment> findByApprovedFalseAndDisableDateIsNullOrderByCreatedAtDesc();

    // ==================== کامنت‌های حذف شده ====================

    // یافتن کامنت‌های حذف شده
    List<Comment> findByDisableDateIsNotNull();
    List<Comment> findByPostIdAndDisableDateIsNotNull(Long postId);
    Page<Comment> findByDisableDateIsNotNull(Pageable pageable);

    // یافتن کامنت‌های حذف شده یک پست
    List<Comment> findByPostIdAndDisableDateIsNotNullOrderByCreatedAtDesc(Long postId);

    // ==================== شمارش‌ها ====================

    // شمارش کامنت‌های فعال
    long countByDisableDateIsNull();
    long countByPostIdAndDisableDateIsNull(Long postId);
    long countByPostIdAndApprovedTrueAndDisableDateIsNull(Long postId);
    long countByPostIdAndApprovedFalseAndDisableDateIsNull(Long postId);

    // شمارش کامنت‌های حذف شده
    long countByDisableDateIsNotNull();
    long countByPostIdAndDisableDateIsNotNull(Long postId);

    // ==================== بررسی وجود ====================

    // بررسی وجود کامنت فعال
    boolean existsByIdAndDisableDateIsNull(Long id);
    boolean existsByPostIdAndDisableDateIsNull(Long postId);
    boolean existsByPostIdAndAuthorEmailAndDisableDateIsNull(Long postId, String authorEmail);
}