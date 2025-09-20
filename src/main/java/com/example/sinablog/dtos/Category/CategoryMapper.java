package com.example.sinablog.dtos.Category;


import com.example.sinablog.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryRequestDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setSlug(dto.getSlug());
        category.setDescription(dto.getDescription());
        return category;
    }

    public CategoryResponseDTO toDTO(Category category) {
        return new CategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }

    public void updateEntityFromDTO(CategoryRequestDTO dto, Category category) {
        if (dto.getName() != null) {
            category.setName(dto.getName());
        }
        if (dto.getSlug() != null) {
            category.setSlug(dto.getSlug());
        }
        if (dto.getDescription() != null) {
            category.setDescription(dto.getDescription());
        }
    }
}