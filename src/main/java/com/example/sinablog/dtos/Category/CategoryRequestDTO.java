package com.example.sinablog.dtos.Category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryRequestDTO {

    @NotBlank(message = "Category.name.is.required")
    @Size(min = 2, max = 50, message = "Category.name.must.be.between.2.and.50.characters")
    private String name;

    @Size(max = 100, message = "Slug.must.not.exceed.100.characters")
    private String slug;

    @Size(max = 500, message = "Description.must.not.exceed.500.characters")
    private String description;


    public CategoryRequestDTO() {}

    public CategoryRequestDTO(String name, String slug, String description) {
        this.name = name;
        this.slug = slug;
        this.description = description;
    }


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}