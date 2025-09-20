package com.example.sinablog.Service.comment;

import com.example.sinablog.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CommentService {

    // ==================== عملیات CRUD ====================
    Comment createComment(Comment comment);
    Comment updateComment(Long id, Comment comment);
    void deleteComment(Long id); // Soft Delete
    Optional<Comment> getCommentById(Long id);

    // ==================== کامنت‌های فعال ====================
    List<Comment> getCommentsByPostId(Long postId);
    Page<Comment> getCommentsByPostId(Long postId, Pageable pageable);
    List<Comment> getApprovedComments();
    List<Comment> getApprovedCommentsByPostId(Long postId);
    Page<Comment> getApprovedCommentsByPostId(Long postId, Pageable pageable);
    List<Comment> getPendingComments();
    List<Comment> getPendingCommentsByPostId(Long postId);
    Page<Comment> getPendingCommentsByPostId(Long postId, Pageable pageable);
    List<Comment> getCommentsByAuthorEmail(String authorEmail);
    List<Comment> getCommentsByPostIdAndAuthorEmail(Long postId, String authorEmail);

    // ==================== مدیریت تایید کامنت ====================
    void approveComment(Long commentId);
    void rejectComment(Long commentId); // Soft Delete

    // ==================== کامنت‌های حذف شده ====================
    List<Comment> getDeletedComments();
    List<Comment> getDeletedCommentsByPostId(Long postId);
    Page<Comment> getDeletedComments(Pageable pageable);
    void restoreComment(Long id);
    void permanentDeleteComment(Long id); // حذف فیزیکی

    // ==================== شمارش‌ها ====================
    long countAllComments();
    long countCommentsByPostId(Long postId);
    long countApprovedCommentsByPostId(Long postId);
    long countPendingCommentsByPostId(Long postId);
    long countDeletedComments();
    long countDeletedCommentsByPostId(Long postId);

    // ==================== کامنت‌های جدید ====================
    List<Comment> getLatestComments(int limit);
    List<Comment> getLatestCommentsByPostId(Long postId, int limit);
    List<Comment> getLatestApprovedComments(int limit);
}