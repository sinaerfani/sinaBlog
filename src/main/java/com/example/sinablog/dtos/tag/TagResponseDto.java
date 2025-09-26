package com.example.sinablog.dtos.tag;
// TagResponseDto.java

public class TagResponseDto {
    private Long id;
    private String name;
    private String slug;
    private int postCount;

    public TagResponseDto(Long id, String name, String slug, int postCount) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.postCount = postCount;
    }

    // getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public int getPostCount() { return postCount; }
}

