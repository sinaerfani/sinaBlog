package com.example.sinablog.Service.Post;
import com.example.sinablog.model.Post;
import com.example.sinablog.model.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface PostService {
    Post createPost(Post post);
    Post updatePost(Long id, Post post);
    void deletePost(Long id);
    Post getPostById(Long id);
    Post getPostBySlug(String slug);
    Page<Post> getPublishedPosts(Pageable pageable);
    Page<Post> getDraftPosts(Pageable pageable);
    Page<Post> searchPosts(String keyword, Pageable pageable);
    List<Post> getPostsByCategory(Long categoryId);
    List<Post> getPostsByTag(Long tagId);
    List<Post> getRecentPosts(int limit);
    void incrementViews(Long postId);
    Page<Post> getPostsByStatus(PostStatus status, Pageable pageable);
}
