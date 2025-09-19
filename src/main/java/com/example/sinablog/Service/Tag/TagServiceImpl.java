package com.example.sinablog.Service.Tag;

import com.example.sinablog.model.Tag;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements  TagService
{
    @Override
    public Tag createTag(Tag tag) {
        return null;
    }

    @Override
    public Tag updateTag(Long id, Tag tag) {
        return null;
    }

    @Override
    public void deleteTag(Long id) {

    }

    @Override
    public Tag getTagById(Long id) {
        return null;
    }

    @Override
    public Tag getTagBySlug(String slug) {
        return null;
    }

    @Override
    public List<Tag> getAllTags() {
        return null;
    }

    @Override
    public List<Tag> getPopularTags(int limit) {
        return null;
    }

    @Override
    public List<Tag> getTagsByPost(Long postId) {
        return null;
    }

    @Override
    public boolean existsByName(String name) {
        return false;
    }

    @Override
    public boolean existsBySlug(String slug) {
        return false;
    }
}
