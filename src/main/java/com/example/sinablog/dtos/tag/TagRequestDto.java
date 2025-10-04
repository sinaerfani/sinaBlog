package com.example.sinablog.dtos.tag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TagRequestDto {
      private String name;


    private String slug;


    @NotBlank(message = "Tag.name.is.required")
    @Size(min = 2, max = 50, message = "Tag.name.must.be.between.2.and.50.characters")

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    @Size(max = 100, message = "Slug.must.be.up.to.100.characters")
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
}

