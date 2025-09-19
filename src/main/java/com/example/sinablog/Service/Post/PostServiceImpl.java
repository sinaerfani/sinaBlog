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
    public Page<Post> getDraftPosts(Pageable pageable) {
        return null;
    }

    @Override
    public Page<Post> searchPosts(String keyword, Pageable pageable) {
        return null;
    }

    @Override
    public Post createPost(Post post) {
        return null;
    }

    @Override
    public Post updatePost(Long id, Post post) {
        return null;
    }

    @Override
    public void deletePost(Long id) {

    }

    @Override
    public Post getPostById(Long id) {
        return null;
    }

    @Override
    public Post getPostBySlug(String slug) {
        return null;
    }


    @Override
    public List<Post> getPostsByCategory(Long categoryId) {
        return postRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<Post> getPostsByTag(Long tagId) {
        return postRepository.findByTagsId(tagId);
    }

    @Override
    public List<Post> getRecentPosts(int limit) {
        return null;
    }

    @Override
    public void incrementViews(Long postId) {

    }

    @Override
    public Page<Post> getPostsByStatus(PostStatus status, Pageable pageable) {
        return null;
    }
}
