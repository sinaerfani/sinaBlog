package com.example.sinablog.Service.Post;

import com.example.sinablog.Repository.PostRepository;
import com.example.sinablog.customeExeption.RuleException;
import com.example.sinablog.model.Post;
import com.example.sinablog.model.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService{
    private final PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public Page<Post> getPublishedPosts(Pageable pageable) {
        return postRepository.findByStatus(PostStatus.PUBLISHED, pageable);
    }

    @Override
    public Optional<Post> getPostBySlug(String slug) {
        return postRepository.findBySlug(slug);
    }

    @Override
    public List<Post> getPostsByCategory(Long categoryId) {
        return postRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<Post> getPostsByTag(Long tagId) {
        return postRepository.findByTagsId(tagId);
    }
}
