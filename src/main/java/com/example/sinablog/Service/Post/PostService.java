package com.example.sinablog.Service.Post;
import com.example.sinablog.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface PostService {
    Page<Post> getPublishedPosts(Pageable pageable);

    Optional<Post> getPostBySlug(String slug);

    List<Post> getPostsByCategory(Long categoryId);

    List<Post> getPostsByTag(Long tagId);
}
