package com.example.sinablog.Service.comment;

import com.example.sinablog.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService{

    @Override
    public Comment createComment(Comment comment) {
        return null;
    }

    @Override
    public Comment updateComment(Long id, Comment comment) {
        return null;
    }

    @Override
    public void deleteComment(Long id) {

    }

    @Override
    public Comment getCommentById(Long id) {
        return null;
    }

    @Override
    public List<Comment> getCommentsByPost(Long postId) {
        return null;
    }

    @Override
    public List<Comment> getPendingComments() {
        return null;
    }

    @Override
    public List<Comment> getApprovedComments() {
        return null;
    }

    @Override
    public void approveComment(Long commentId) {

    }

    @Override
    public void rejectComment(Long commentId) {

    }

    @Override
    public Page<Comment> getCommentsByStatus(boolean approved, Pageable pageable) {
        return null;
    }
}
