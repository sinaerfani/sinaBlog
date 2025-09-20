package com.example.sinablog.Service.Post;


import com.example.sinablog.model.Post;
import com.example.sinablog.model.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PostService {

    // ==================== عملیات CRUD ====================
    Post createPost(Post post);
    Post updatePost(Long id, Post post);
    void deletePost(Long id); // Soft Delete
    Optional<Post> getPostById(Long id);
    Optional<Post> getPostBySlug(String slug);

    // ==================== پست‌های فعال ====================
    List<Post> getAllPosts();
    Page<Post> getAllPosts(Pageable pageable);
    List<Post> getPostsByStatus(PostStatus status);
    Page<Post> getPostsByStatus(PostStatus status, Pageable pageable);
    List<Post> getPublishedPosts();
    Page<Post> getPublishedPosts(Pageable pageable);
    List<Post> getDraftPosts();
    List<Post> getPostsByCategory(Long categoryId);
    List<Post> getPublishedPostsByCategory(Long categoryId);
    List<Post> getPostsByAuthor(Long authorId);
    List<Post> getPublishedPostsByAuthor(Long authorId);
    List<Post> getPostsByTag(Long tagId);
    List<Post> getPublishedPostsByTag(Long tagId);

    // ==================== جستجو ====================
    List<Post> searchPosts(String keyword);

    List<Post> searchPublishedPosts(String keyword);

    // ==================== پست‌های حذف شده ====================
    List<Post> getDeletedPosts();
    List<Post> getDeletedPostsByAuthor(Long authorId);
    void restorePost(Long id);
    void permanentDeletePost(Long id); // حذف فیزیکی

    // ==================== مدیریت وضعیت ====================
    void changePostStatus(Long id, PostStatus status);
    void publishPost(Long id);
    void unpublishPost(Long id);

    // ==================== شمارش‌ها ====================
    long countAllPosts();
    long countPublishedPosts();
    long countDraftPosts();
    long countPostsByCategory(Long categoryId);
    long countPostsByAuthor(Long authorId);
    long countDeletedPosts();

    // ==================== پست‌های جدید ====================
    List<Post> getLatestPosts(int limit);
    List<Post> getLatestPublishedPosts(int limit);
    List<Post> getLatestPostsByCategory(Long categoryId, int limit);
    List<Post> getLatestPublishedPostsByCategory(Long categoryId, int limit);

    // ==================== افزایش بازدید ====================

}
