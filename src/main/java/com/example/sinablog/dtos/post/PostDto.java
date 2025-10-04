package com.example.sinablog.dtos.post;


import com.example.sinablog.model.enums.PostStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class PostDto {

    private Long id;

    @NotBlank(message = "Title.is.required")
    @Size(min = 3, max = 200, message = "Title.must.be.between.3.and.200.characters")
    private String title;

    private String slug;

    @NotBlank(message = "Content.is.required")
    @Size(min = 10, message = "Content.must.be.at.least.10.characters")
    private String content;

    @Size(max = 500, message = "Excerpt.must.be.less.than.500.characters")
    private String excerpt;

    private PostStatus status;


    private Long authorId;

    private Long categoryId;

    private List<Long> tagIds;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public PostStatus getStatus() {
        return status;
    }

    public void setStatus(PostStatus status) {
        this.status = status;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }
}
