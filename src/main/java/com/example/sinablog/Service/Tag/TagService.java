package com.example.sinablog.Service.Tag;

import com.example.sinablog.model.Tag;

import java.util.List;

public interface TagService {
    Tag createTag(Tag tag);
    Tag updateTag(Long id, Tag tag);
    void deleteTag(Long id);
    Tag getTagById(Long id);
    Tag getTagBySlug(String slug);
    List<Tag> getAllTags();
    List<Tag> getPopularTags(int limit);
    List<Tag> getTagsByPost(Long postId);
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
}
