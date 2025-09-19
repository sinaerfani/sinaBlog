package com.example.sinablog.Service.Tag;

import com.example.sinablog.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;


public interface TagService {
    Tag createTag(Tag tag);
    Tag updateTag(Long id, Tag tag);
    void deleteTag(Long id);
    Optional<Tag> getTagById(Long id);
    Optional<Tag> getTagByName(String name);
    Optional<Tag> getTagBySlug(String slug);
    List<Tag> getAllTags();
    Page<Tag> getAllTags(Pageable pageable);
    List<Tag> searchTagsByName(String name);
    List<Tag> searchTagsBySlug(String slug);
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
    long countTags();

    List<Tag> getTagsByPostId(Long postId);
    List<Tag> getOrCreateTags(List<String> tagNames);
}
