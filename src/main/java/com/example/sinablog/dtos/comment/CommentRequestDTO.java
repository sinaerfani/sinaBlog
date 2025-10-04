package com.example.sinablog.dtos.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CommentRequestDTO {

    @NotBlank(message = "Comment.content.is.required")
    private String content;

    @NotNull(message = "Post.ID.is.required")
    private Long postId;


    public CommentRequestDTO() {}

    public CommentRequestDTO(String content, Long postId) {
        this.content = content;
        this.postId = postId;
    }


    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
}