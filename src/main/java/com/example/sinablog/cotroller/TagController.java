package com.example.sinablog.controller.tag;

import com.example.sinablog.Service.Tag.TagService;
import com.example.sinablog.customeExeption.RuleException;
import com.example.sinablog.dtos.tag.TagRequestDto;
import com.example.sinablog.dtos.tag.TagResponseDto;
import com.example.sinablog.model.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public ResponseEntity<List<TagResponseDto>> getAllTags() {
        List<Tag> tags = tagService.getAllTags();
        List<TagResponseDto> response = tags.stream()
                .map(tag -> new TagResponseDto(
                        tag.getId(),
                        tag.getName(),
                        tag.getSlug(),
                        tag.getPosts() != null ? tag.getPosts().size() : 0
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<TagResponseDto> getTagById(@PathVariable Long id) {
        Tag tag = tagService.getTagById(id)
                .orElseThrow(() -> new RuleException("Tag    not found with ID"));;
        TagResponseDto response = new TagResponseDto(
                tag.getId(),
                tag.getName(),
                tag.getSlug(),
                tag.getPosts() != null ? tag.getPosts().size() : 0
        );
        return ResponseEntity.ok(response);
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TagResponseDto> createTag(@Valid @RequestBody TagRequestDto dto) {
        Tag tag = new Tag();
        tag.setName(dto.getName());
        tag.setSlug(dto.getSlug());

        Tag created = tagService.createTag(tag);

        TagResponseDto response = new TagResponseDto(
                created.getId(),
                created.getName(),
                created.getSlug(),
                created.getPosts() != null ? created.getPosts().size() : 0
        );
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TagResponseDto> updateTag(@PathVariable Long id,
                                                    @Valid @RequestBody TagRequestDto dto) {
        Tag tag = new Tag();
        tag.setName(dto.getName());
        tag.setSlug(dto.getSlug());

        Tag updated = tagService.updateTag(id, tag);

        TagResponseDto response = new TagResponseDto(
                updated.getId(),
                updated.getName(),
                updated.getSlug(),
                updated.getPosts() != null ? updated.getPosts().size() : 0
        );
        return ResponseEntity.ok(response);
    }

    // 🔴 حذف تگ (فقط ادمین)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}
