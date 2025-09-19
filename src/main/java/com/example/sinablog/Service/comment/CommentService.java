package com.example.sinablog.Service.comment;

import com.example.sinablog.model.Comment;
import org.springframework.data.domain.Page;

import java.awt.print.Pageable;
import java.util.List;

public interface CommentService {
    Comment createComment(Comment comment);
    Comment updateComment(Long id, Comment comment);
    void deleteComment(Long id);
    Comment getCommentById(Long id);
    List<Comment> getCommentsByPost(Long postId);
    List<Comment> getPendingComments();
    List<Comment> getApprovedComments();
    void approveComment(Long commentId);
    void rejectComment(Long commentId);
    Page<Comment> getCommentsByStatus(boolean approved, Pageable pageable);

}
