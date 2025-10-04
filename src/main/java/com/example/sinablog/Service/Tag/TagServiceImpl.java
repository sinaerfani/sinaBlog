package com.example.sinablog.Service.Tag;


import com.example.sinablog.Repository.TagRepository;
import com.example.sinablog.customeExeption.RuleException;
import com.example.sinablog.model.Tag;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TagServiceImpl implements  TagService
{
    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Tag createTag(Tag tag) {


        if (tag.getName() == null || tag.getName().trim().isEmpty()) {
            throw new RuleException("Tag.name.cannot.be.null.or.empty");
        }

        if (tagRepository.existsByName(tag.getName())) {
            throw new RuleException("Tag.already.exists");
        }

        if (tag.getSlug() == null || tag.getSlug().trim().isEmpty()) {
            tag.setSlug(generateSlug(tag.getName()));
        }

        if (tagRepository.existsBySlug(tag.getSlug())) {
            throw new RuleException("Tag.with.slug.already.exists");
        }

        return tagRepository.save(tag);
    }

    @Override
    public Tag updateTag(Long id, Tag tag) {

        if (id == null) {
            throw new RuleException("Tag.ID.cannot.be.null");
        }

        Tag existingTag = tagRepository.findById(id)
                .orElseThrow(() -> new RuleException("Tag.not.found.with.ID"));

        if (tag.getName() != null && !tag.getName().trim().isEmpty()) {
            // بررسی تکراری نبودن name
            if (!existingTag.getName().equals(tag.getName()) &&
                    tagRepository.existsByName(tag.getName())) {
                throw new RuleException("Tag.already.exists");
            }
            existingTag.setName(tag.getName());
        }

        if (tag.getSlug() != null && !tag.getSlug().trim().isEmpty()) {
            // بررسی تکراری نبودن slug
            if (!existingTag.getSlug().equals(tag.getSlug()) &&
                    tagRepository.existsBySlug(tag.getSlug())) {
                throw new RuleException("Tag.with.slug.already.exists");
            }
            existingTag.setSlug(tag.getSlug());
        }

        return tagRepository.save(existingTag);
    }

    @Override
    public void deleteTag(Long id) {
        if (id == null) {
            throw new RuleException("Tag.ID.cannot.be.null");
        }

        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new RuleException("Tag.not.found.with.ID"));

        // بررسی اینکه تگ در حال استفاده نباشد
        if (tag.getPosts() != null && !tag.getPosts().isEmpty()) {
            throw new RuleException("Cannot.delete.tag.There.are.posts.assigned.to.this.tag");
        }

        tagRepository.delete(tag);
    }

    @Override
    public Optional<Tag> getTagById(Long id) {
        if (id == null) {
            throw new RuleException("Tag.ID.cannot.be.null");
        }

        return tagRepository.findById(id);
    }

    @Override
    public Optional<Tag> getTagByName(String name) {

        if (name == null || name.trim().isEmpty()) {
            throw new RuleException("Tag.name.cannot.be.null.or.empty");
        }

        return tagRepository.findByName(name);
    }

    @Override
    public Optional<Tag> getTagBySlug(String slug) {

        if (slug == null || slug.trim().isEmpty()) {
            throw new RuleException("Tag.slug.cannot.be.null.or.empty");
        }

        return tagRepository.findBySlug(slug);
    }

    @Override
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @Override
    public Page<Tag> getAllTags(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }


    @Override
    public boolean existsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new RuleException("Tag.name.cannot.be.null.or.empty");
        }

        return tagRepository.existsByName(name);
    }

    @Override
    public boolean existsBySlug(String slug) {
        if (slug == null || slug.trim().isEmpty()) {
            throw new RuleException("Tag.slug.cannot.be.null.or.empty");
        }

        return tagRepository.existsBySlug(slug);
    }



    @Override
    public List<Tag> getTagsByPostId(Long postId) {

        if (postId == null) {
            throw new RuleException("Post.ID.cannot.be.null");
        }

        return tagRepository.findByPosts_Id(postId);
    }

    @Override
    public List<Tag> getOrCreateTags(List<String> tagNames) {

        if (tagNames == null || tagNames.isEmpty()) {
            return new ArrayList<>();
        }

        List<Tag> tags = new ArrayList<>();

        for (String tagName : tagNames) {
            if (tagName != null && !tagName.trim().isEmpty()) {
                Tag tag = tagRepository.findByName(tagName.trim())
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setName(tagName.trim());
                            newTag.setSlug(generateSlug(tagName.trim()));
                            return tagRepository.save(newTag);
                        });
                tags.add(tag);
            }
        }

        return tags;
    }

    // متد کمکی برای تولید slug
    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\u0600-\\u06FF\\s]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }

}
