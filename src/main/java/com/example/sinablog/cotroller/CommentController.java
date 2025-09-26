package com.example.sinablog.cotroller;


import com.example.sinablog.Service.User.UserService;
import com.example.sinablog.Service.comment.CommentService;
import com.example.sinablog.customeExeption.RuleException;
import com.example.sinablog.dtos.comment.CommentMapper;
import com.example.sinablog.dtos.comment.CommentRequestDTO;
import com.example.sinablog.dtos.comment.CommentResponseDTO;
import com.example.sinablog.model.Comment;
import com.example.sinablog.model.User;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
@Validated
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final UserService userService;

    public CommentController(CommentService commentService, CommentMapper commentMapper, UserService userService) {
        this.commentService = commentService;
        this.commentMapper = commentMapper;
        this.userService = userService;
    }

    // ==================== عملیات CRUD ====================

    /**
     * ایجاد کامنت جدید - فقط کاربران authenticated
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentResponseDTO> createComment(
            @Valid @RequestBody CommentRequestDTO commentRequestDTO,
            Principal principal) {

        try {
            // بررسی وجود principal
            if (principal == null) {
                throw new RuleException("User not authenticated");
            }

            // یافتن کاربر بر اساس نام کاربری
            User currentUser = userService.getUserByUsername(principal.getName())
                    .orElseThrow(() -> new RuleException("User not found"));

            Comment comment = commentMapper.toEntity(commentRequestDTO);
            comment.setAuthorName(currentUser.getFullName());
            comment.setAuthorEmail(currentUser.getEmail());

            Comment createdComment = commentService.createComment(comment);
            CommentResponseDTO responseDTO = commentMapper.toDTO(createdComment);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Error creating comment: " + e.getMessage());
        }
    }

    /**
     * به‌روزرسانی کامنت - فقط نویسنده کامنت یا ادمین
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentRequestDTO commentRequestDTO,
            @AuthenticationPrincipal User currentUser) {

        try {
            // بررسی مالکیت کامنت
            Comment existingComment = commentService.getCommentById(id)
                    .orElseThrow(() -> new RuleException("Comment not found"));

            if (!isCommentOwner(existingComment, currentUser) && !isAdmin(currentUser)) {
                throw new RuleException("You can only update your own comments");
            }

            Comment comment = commentMapper.toEntity(commentRequestDTO);
            Comment updatedComment = commentService.updateComment(id, comment);
            CommentResponseDTO responseDTO = commentMapper.toDTO(updatedComment);

            return ResponseEntity.ok(responseDTO);

        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Error updating comment: " + e.getMessage());
        }
    }

    /**
     * حذف کامنت - فقط نویسنده کامنت یا ادمین
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {

        try {
            // بررسی مالکیت کامنت
            Comment existingComment = commentService.getCommentById(id)
                    .orElseThrow(() -> new RuleException("Comment not found"));

            if (!isCommentOwner(existingComment, currentUser) && !isAdmin(currentUser)) {
                throw new RuleException("You can only delete your own comments");
            }

            commentService.deleteComment(id);
            return ResponseEntity.noContent().build();

        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Error deleting comment: " + e.getMessage());
        }
    }

    /**
     * دریافت کامنت بر اساس ID - برای همه قابل دسترسی
     */
    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseDTO> getCommentById(@PathVariable Long id) {
        try {
            Comment comment = commentService.getCommentById(id)
                    .orElseThrow(() -> new RuleException("Comment not found with ID: " + id));

            // فقط کامنت‌های تأیید شده یا کامنت‌های کاربر خودش
            if (!comment.isApproved()) {
                throw new RuleException("Comment is not approved yet");
            }

            CommentResponseDTO responseDTO = commentMapper.toDTO(comment);
            return ResponseEntity.ok(responseDTO);

        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Error retrieving comment: " + e.getMessage());
        }
    }

    // ==================== کامنت‌های پست ====================

    /**
     * دریافت کامنت‌های یک پست - فقط کامنت‌های تأیید شده
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByPostId(@PathVariable Long postId) {
        try {
            List<Comment> comments = commentService.getApprovedCommentsByPostId(postId);
            List<CommentResponseDTO> responseDTOs = comments.stream()
                    .map(commentMapper::toDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responseDTOs);

        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Error retrieving comments: " + e.getMessage());
        }
    }

    /**
     * دریافت کامنت‌های یک پست با صفحه‌بندی - فقط کامنت‌های تأیید شده
     */
    @GetMapping("/post/{postId}/paged")
    public ResponseEntity<Page<CommentResponseDTO>> getCommentsByPostId(
            @PathVariable Long postId,
            Pageable pageable) {

        try {
            Page<Comment> comments = commentService.getApprovedCommentsByPostId(postId, pageable);
            Page<CommentResponseDTO> responseDTOs = comments.map(commentMapper::toDTO);

            return ResponseEntity.ok(responseDTOs);

        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Error retrieving comments: " + e.getMessage());
        }
    }

    // ==================== مدیریت کامنت‌ها (فقط ادمین) ====================

    /**
     * دریافت تمام کامنت‌های در انتظار تأیید - فقط ادمین
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CommentResponseDTO>> getPendingComments() {
        try {
            List<Comment> comments = commentService.getPendingComments();
            List<CommentResponseDTO> responseDTOs = comments.stream()
                    .map(commentMapper::toDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responseDTOs);

        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Error retrieving pending comments: " + e.getMessage());
        }
    }

    /**
     * دریافت کامنت‌های در انتظار تأیید یک پست - فقط ادمین
     */
    @GetMapping("/post/{postId}/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CommentResponseDTO>> getPendingCommentsByPostId(@PathVariable Long postId) {
        try {
            List<Comment> comments = commentService.getPendingCommentsByPostId(postId);
            List<CommentResponseDTO> responseDTOs = comments.stream()
                    .map(commentMapper::toDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responseDTOs);

        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Error retrieving pending comments: " + e.getMessage());
        }
    }

    /**
     * تأیید کامنت - فقط ادمین
     */
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommentResponseDTO> approveComment(@PathVariable Long id) {
        try {
            commentService.approveComment(id);
            Comment comment = commentService.getCommentById(id)
                    .orElseThrow(() -> new RuleException("Comment not found"));

            CommentResponseDTO responseDTO = commentMapper.toDTO(comment);
            return ResponseEntity.ok(responseDTO);

        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Error approving comment: " + e.getMessage());
        }
    }

    /**
     * رد کامنت - فقط ادمین
     */
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> rejectComment(@PathVariable Long id) {
        try {
            commentService.rejectComment(id);
            return ResponseEntity.noContent().build();

        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Error rejecting comment: " + e.getMessage());
        }
    }

    // ==================== کامنت‌های حذف شده (فقط ادمین) ====================

    /**
     * دریافت کامنت‌های حذف شده - فقط ادمین
     */
    @GetMapping("/deleted")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CommentResponseDTO>> getDeletedComments() {
        try {
            List<Comment> comments = commentService.getDeletedComments();
            List<CommentResponseDTO> responseDTOs = comments.stream()
                    .map(commentMapper::toDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responseDTOs);

        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Error retrieving deleted comments: " + e.getMessage());
        }
    }

    /**
     * بازیابی کامنت حذف شده - فقط ادمین
     */
    @PostMapping("/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommentResponseDTO> restoreComment(@PathVariable Long id) {
        try {
            commentService.restoreComment(id);
            Comment comment = commentService.getCommentById(id)
                    .orElseThrow(() -> new RuleException("Comment not found"));

            CommentResponseDTO responseDTO = commentMapper.toDTO(comment);
            return ResponseEntity.ok(responseDTO);

        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Error restoring comment: " + e.getMessage());
        }
    }

    /**
     * حذف دائمی کامنت - فقط ادمین
     */
    @DeleteMapping("/{id}/permanent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> permanentDeleteComment(@PathVariable Long id) {
        try {
            commentService.permanentDeleteComment(id);
            return ResponseEntity.noContent().build();

        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Error permanently deleting comment: " + e.getMessage());
        }
    }

    // ==================== کامنت‌های کاربر جاری ====================

    /**
     * دریافت کامنت‌های کاربر جاری
     */
    @GetMapping("/my-comments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CommentResponseDTO>> getMyComments(
            @AuthenticationPrincipal User currentUser) {

        try {
            List<Comment> comments = commentService.getCommentsByAuthorEmail(currentUser.getEmail());
            List<CommentResponseDTO> responseDTOs = comments.stream()
                    .map(commentMapper::toDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responseDTOs);

        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Error retrieving your comments: " + e.getMessage());
        }
    }

    /**
     * دریافت کامنت‌های کاربر جاری برای یک پست خاص
     */
    @GetMapping("/post/{postId}/my-comments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CommentResponseDTO>> getMyCommentsByPostId(
            @PathVariable Long postId,
            @AuthenticationPrincipal User currentUser) {

        try {
            List<Comment> comments = commentService.getCommentsByPostIdAndAuthorEmail(
                    postId, currentUser.getEmail());
            List<CommentResponseDTO> responseDTOs = comments.stream()
                    .map(commentMapper::toDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responseDTOs);

        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Error retrieving your comments: " + e.getMessage());
        }
    }

    // ==================== آمار و اطلاعات ====================

    /**
     * تعداد کامنت‌های یک پست - برای همه قابل دسترسی
     */
    @GetMapping("/post/{postId}/count")
    public ResponseEntity<Long> countCommentsByPostId(@PathVariable Long postId) {
        try {
            long count = commentService.countApprovedCommentsByPostId(postId);
            return ResponseEntity.ok(count);

        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Error counting comments: " + e.getMessage());
        }
    }

    /**
     * تعداد کامنت‌های در انتظار تأیید - فقط ادمین
     */
    @GetMapping("/pending/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> countPendingComments() {
        try {
            long count = commentService.getPendingComments().size();
            return ResponseEntity.ok(count);

        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Error counting pending comments: " + e.getMessage());
        }
    }

    // ==================== متدهای کمکی ====================

    private boolean isCommentOwner(Comment comment, User user) {
        return comment.getAuthorEmail().equals(user.getEmail());
    }

    private boolean isAdmin(User user) {
        return user.getRole() != null && "ADMIN".equals(user.getRole().getName().name());
    }

}