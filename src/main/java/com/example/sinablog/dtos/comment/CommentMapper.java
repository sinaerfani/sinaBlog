package com.example.sinablog.dtos.comment;

import com.example.sinablog.Repository.PostRepository;
import com.example.sinablog.model.Comment;
import com.example.sinablog.model.Post;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    private final PostRepository postRepository;

    public CommentMapper(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Comment toEntity(CommentRequestDTO dto) {
        Comment comment = new Comment();
        comment.setContent(dto.getContent());

        // تنظیم post از طریق ID
        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new RuntimeException("Post.not.found"));
        comment.setPost(post);

        return comment;
    }

    public CommentResponseDTO toDTO(Comment comment) {
        return new CommentResponseDTO(
                comment.getId(),
                comment.getContent(),
                comment.getAuthorName(),
                comment.getAuthorEmail(),
                comment.getPost().getId(),
                comment.getPost().getTitle(),
                comment.isApproved(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}