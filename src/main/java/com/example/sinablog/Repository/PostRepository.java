package com.example.sinablog.Repository;
import com.example.sinablog.model.Post;
import com.example.sinablog.model.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findBySlug(String slug);
    Page<Post> findByStatus(PostStatus status, Pageable pageable);
    List<Post> findByCategoryId(Long categoryId);
    List<Post> findByTagsId(Long tagId); // توجه: باید Tags باشد نه Tag
}