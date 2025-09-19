package com.example.sinablog.Repository;

import com.example.sinablog.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);

    Optional<Tag> findBySlug(String slug);

    boolean existsByName(String name);

    boolean existsBySlug(String slug);

    List<Tag> findByNameContainingIgnoreCase(String name);

    List<Tag> findBySlugContaining(String slug);

    Page<Tag> findAll(Pageable pageable);

    List<Tag> findByPosts_Id(Long postId);

    long count();


}
