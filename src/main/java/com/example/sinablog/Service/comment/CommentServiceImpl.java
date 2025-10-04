package com.example.sinablog.Service.comment;

import com.example.sinablog.Repository.CommentRepository;
import com.example.sinablog.customeExeption.RuleException;
import com.example.sinablog.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service

public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    // ==================== عملیات CRUD ====================

    @Override
    public Comment createComment(Comment comment) {
        validateComment(comment);

        comment.setApproved(false); // پیش‌فرض تأیید نشده
        comment.setDisableDate(null); // پیش‌فرض فعال
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    @Override
    public Comment updateComment(Long id, Comment comment) {
        if (id == null) {
            throw new RuleException("Comment.ID.cannot.be.null");
        }

        Comment existingComment = getActiveCommentById(id);

        if (comment.getContent() != null) {
            existingComment.setContent(comment.getContent().trim());
        }
        if (comment.getAuthorName() != null) {
            existingComment.setAuthorName(comment.getAuthorName().trim());
        }
        if (comment.getAuthorEmail() != null) {
            existingComment.setAuthorEmail(comment.getAuthorEmail().trim());
        }

        existingComment.setUpdatedAt(LocalDateTime.now());
        return commentRepository.save(existingComment);
    }

    @Override
    public void deleteComment(Long id) {
        Comment comment = getActiveCommentById(id);
        performSoftDelete(comment);
    }

    @Override
    public Optional<Comment> getCommentById(Long id) {
        if (id == null) {
            throw new RuleException("Comment.ID.cannot.be.null");
        }
        return commentRepository.findByIdAndDisableDateIsNull(id);
    }

    // ==================== کامنت‌های فعال ====================

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        validatePostId(postId);
        return commentRepository.findByPostIdAndDisableDateIsNull(postId);
    }

    @Override
    public Page<Comment> getCommentsByPostId(Long postId, Pageable pageable) {
        validatePostId(postId);
        return commentRepository.findByPostIdAndDisableDateIsNull(postId, pageable);
    }

    @Override
    public List<Comment> getApprovedComments() {
        return commentRepository.findByApprovedTrueAndDisableDateIsNull();
    }

    @Override
    public List<Comment> getApprovedCommentsByPostId(Long postId) {
        validatePostId(postId);
        return commentRepository.findByPostIdAndApprovedTrueAndDisableDateIsNull(postId);
    }

    @Override
    public Page<Comment> getApprovedCommentsByPostId(Long postId, Pageable pageable) {
        validatePostId(postId);
        return commentRepository.findByPostIdAndApprovedTrueAndDisableDateIsNull(postId, pageable);
    }

    @Override
    public List<Comment> getPendingComments() {
        return commentRepository.findByApprovedFalseAndDisableDateIsNull();
    }

    @Override
    public List<Comment> getPendingCommentsByPostId(Long postId) {
        validatePostId(postId);
        return commentRepository.findByPostIdAndApprovedFalseAndDisableDateIsNull(postId);
    }

    @Override
    public Page<Comment> getPendingCommentsByPostId(Long postId, Pageable pageable) {
        validatePostId(postId);
        return commentRepository.findByPostIdAndApprovedFalseAndDisableDateIsNull(postId, pageable);
    }

    @Override
    public List<Comment> getCommentsByAuthorEmail(String authorEmail) {
        validateEmail(authorEmail);
        return commentRepository.findByAuthorEmailAndDisableDateIsNull(authorEmail);
    }

    @Override
    public List<Comment> getCommentsByPostIdAndAuthorEmail(Long postId, String authorEmail) {
        validatePostId(postId);
        validateEmail(authorEmail);
        return commentRepository.findByPostIdAndAuthorEmailAndDisableDateIsNull(postId, authorEmail);
    }

    // ==================== مدیریت تایید کامنت ====================

    @Override
    public void approveComment(Long commentId) {
        Comment comment = getActiveCommentById(commentId);
        comment.setApproved(true);
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }

    @Override
    public void rejectComment(Long commentId) {
        Comment comment = getActiveCommentById(commentId);
        performSoftDelete(comment);
    }

    // ==================== کامنت‌های حذف شده ====================

    @Override
    public List<Comment> getDeletedComments() {
        return commentRepository.findByDisableDateIsNotNull();
    }

    @Override
    public List<Comment> getDeletedCommentsByPostId(Long postId) {
        validatePostId(postId);
        return commentRepository.findByPostIdAndDisableDateIsNotNull(postId);
    }

    @Override
    public Page<Comment> getDeletedComments(Pageable pageable) {
        return commentRepository.findByDisableDateIsNotNull(pageable);
    }

    @Override
    public void restoreComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuleException("Comment.not.found.with.ID"));

        if (comment.getDisableDate() == null) {
            throw new RuleException("Comment.is.not.deleted");
        }

        comment.setDisableDate(null);
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void permanentDeleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuleException("Comment.not.found.with.ID"));
        commentRepository.delete(comment);
    }

    // ==================== شمارش‌ها ====================

    @Override
    public long countAllComments() {
        return commentRepository.countByDisableDateIsNull();
    }

    @Override
    public long countCommentsByPostId(Long postId) {
        validatePostId(postId);
        return commentRepository.countByPostIdAndDisableDateIsNull(postId);
    }

    @Override
    public long countApprovedCommentsByPostId(Long postId) {
        validatePostId(postId);
        return commentRepository.countByPostIdAndApprovedTrueAndDisableDateIsNull(postId);
    }

    @Override
    public long countPendingCommentsByPostId(Long postId) {
        validatePostId(postId);
        return commentRepository.countByPostIdAndApprovedFalseAndDisableDateIsNull(postId);
    }

    @Override
    public long countDeletedComments() {
        return commentRepository.countByDisableDateIsNotNull();
    }

    @Override
    public long countDeletedCommentsByPostId(Long postId) {
        validatePostId(postId);
        return commentRepository.countByPostIdAndDisableDateIsNotNull(postId);
    }

    // ==================== کامنت‌های جدید ====================

    @Override
    public List<Comment> getLatestComments(int limit) {
        validateLimit(limit);
        List<Comment> comments = commentRepository.findByDisableDateIsNullOrderByCreatedAtDesc();
        return comments.stream().limit(limit).toList();
    }

    @Override
    public List<Comment> getLatestCommentsByPostId(Long postId, int limit) {
        validatePostId(postId);
        validateLimit(limit);
        List<Comment> comments = commentRepository.findByPostIdAndDisableDateIsNullOrderByCreatedAtDesc(postId);
        return comments.stream().limit(limit).toList();
    }

    @Override
    public List<Comment> getLatestApprovedComments(int limit) {
        validateLimit(limit);
        List<Comment> comments = commentRepository.findByApprovedTrueAndDisableDateIsNullOrderByCreatedAtDesc();
        return comments.stream().limit(limit).toList();
    }


    // ==================== متدهای کمکی ====================

    private Comment getActiveCommentById(Long id) {
        return commentRepository.findByIdAndDisableDateIsNull(id)
                .orElseThrow(() -> new RuleException("Comment.not.found.with.ID"));
    }

    private void performSoftDelete(Comment comment) {
        comment.setDisableDate(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }

    private void validateComment(Comment comment) {
        if (comment == null) {
            throw new RuleException("Comment.cannot.be.null");
        }
        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            throw new RuleException("Comment.content.cannot.be.empty");
        }
        if (comment.getAuthorName() == null || comment.getAuthorName().trim().isEmpty()) {
            throw new RuleException("Author.name.cannot.be.empty");
        }
        if (comment.getAuthorEmail() == null || comment.getAuthorEmail().trim().isEmpty()) {
            throw new RuleException("Author.email.cannot.be.empty");
        }
        if (comment.getPost() == null || comment.getPost().getId() == null) {
            throw new RuleException("Comment.must.be.associated.with.a.post");
        }
    }

    private void validatePostId(Long postId) {
        if (postId == null) {
            throw new RuleException("Post.ID.cannot.be.null");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new RuleException("Email.cannot.be.empty");
        }
    }

    private void validateLimit(int limit) {
        if (limit <= 0) {
            throw new RuleException("Limit.must.be.greater.than.0");
        }
    }
}