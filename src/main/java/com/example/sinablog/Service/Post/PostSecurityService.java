package com.example.sinablog.Service.Post;

import com.example.sinablog.Repository.PostRepository;
import com.example.sinablog.model.Post;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostSecurityService {

    private final PostRepository postRepository;

    public PostSecurityService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public boolean isPostAuthor(Long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String username = authentication.getName();
        Optional<Post> post = postRepository.findById(postId);

        return post.isPresent() &&
                post.get().getAuthor() != null &&
                post.get().getAuthor().getUsername().equals(username);
    }
}
