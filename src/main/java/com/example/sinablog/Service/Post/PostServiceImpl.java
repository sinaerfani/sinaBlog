package com.example.sinablog.Service.Post;

import com.example.sinablog.Repository.PostRepository;
import com.example.sinablog.customeExeption.RuleException;
import com.example.sinablog.model.Post;
import com.example.sinablog.model.enums.PostStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // ==================== عملیات CRUD ====================

    @Override
    public Post createPost(Post post) {
        validatePost(post);

        if (post.getStatus() == null) {
            post.setStatus(PostStatus.DRAFT);
        }

        if (post.getSlug() == null || post.getSlug().trim().isEmpty()) {
            post.setSlug(generateSlug(post.getTitle()));
        }


        if (post.getStatus() == PostStatus.PUBLISHED && post.getPublishedAt() == null) {
            post.setPublishedAt(LocalDateTime.now());
        }

        return postRepository.save(post);
    }

    @Override
    public Post updatePost(Long id, Post post) {
        Post existingPost = getActivePostById(id);

        if (post.getTitle() != null) {
            existingPost.setTitle(post.getTitle());
        }
        if (post.getContent() != null) {
            existingPost.setContent(post.getContent());
        }
        if (post.getExcerpt() != null) {
            existingPost.setExcerpt(post.getExcerpt());
        }
        if (post.getCategory() != null) {
            existingPost.setCategory(post.getCategory());
        }
        if (post.getTags() != null) {
            existingPost.setTags(post.getTags());
        }

        existingPost.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(existingPost);
    }

    @Override
    public void deletePost(Long id) {
        Post post = getActivePostById(id);
        performSoftDelete(post);
    }

    @Override
    public Optional<Post> getPostById(Long id) {
        return postRepository.findByIdAndDisableDateIsNull(id);
    }

    @Override
    public Optional<Post> getPostBySlug(String slug) {
        if (slug == null || slug.trim().isEmpty()) {
            throw new RuleException("Slug.cannot.be.empty");
        }
        return postRepository.findBySlugAndDisableDateIsNull(slug);
    }

    // ==================== پست‌های فعال ====================

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findByDisableDateIsNullOrderByCreatedAtDesc();
    }

    @Override
    public Page<Post> getAllPosts(Pageable pageable) {
        return postRepository.findByDisableDateIsNull(pageable);
    }

    @Override
    public List<Post> getPostsByStatus(PostStatus status) {
        validateStatus(status);
        return postRepository.findByStatusAndDisableDateIsNull(status);
    }

    @Override
    public Page<Post> getPostsByStatus(PostStatus status, Pageable pageable) {
        validateStatus(status);
        return postRepository.findByStatusAndDisableDateIsNull(status, pageable);
    }

    @Override
    public List<Post> getPublishedPosts() {
        return postRepository.findByStatusAndDisableDateIsNull(PostStatus.PUBLISHED);
    }

    @Override
    public Page<Post> getPublishedPosts(Pageable pageable) {
        return postRepository.findByStatusAndDisableDateIsNull(PostStatus.PUBLISHED, pageable);
    }

    @Override
    public List<Post> getDraftPosts() {
        return postRepository.findByStatusAndDisableDateIsNull(PostStatus.DRAFT);
    }

    @Override
    public List<Post> getPostsByCategory(Long categoryId) {
        validateId(categoryId, "Category ID");
        return postRepository.findByCategoryIdAndDisableDateIsNull(categoryId);
    }

    @Override
    public List<Post> getPublishedPostsByCategory(Long categoryId) {
        validateId(categoryId, "Category ID");
        return postRepository.findByCategoryIdAndStatusAndDisableDateIsNull(categoryId, PostStatus.PUBLISHED);
    }

    @Override
    public List<Post> getPostsByAuthor(Long authorId) {
        validateId(authorId, "Author ID");
        return postRepository.findByAuthorIdAndDisableDateIsNull(authorId);
    }

    @Override
    public List<Post> getPublishedPostsByAuthor(Long authorId) {
        validateId(authorId, "Author ID");
        return postRepository.findByAuthorIdAndStatusAndDisableDateIsNull(authorId, PostStatus.PUBLISHED);
    }

    @Override
    public List<Post> getPostsByTag(Long tagId) {
        validateId(tagId, "Tag ID");
        return postRepository.findByTagsIdAndDisableDateIsNull(tagId);
    }

    @Override
    public List<Post> getPublishedPostsByTag(Long tagId) {
        validateId(tagId, "Tag ID");
        return postRepository.findByTagsIdAndStatusAndDisableDateIsNull(tagId, PostStatus.PUBLISHED);
    }

    // ==================== جستجو ====================

    @Override
    public List<Post> searchPosts(String keyword) {
        validateKeyword(keyword);
        return postRepository.searchInActivePosts(keyword);
    }


    @Override
    public List<Post> searchPublishedPosts(String keyword) {
        validateKeyword(keyword);
        List<Post> posts = postRepository.searchInActivePosts(keyword);
        return posts.stream()
                .filter(post -> post.getStatus() == PostStatus.PUBLISHED)
                .toList();
    }

    // ==================== پست‌های حذف شده ====================

    @Override
    public List<Post> getDeletedPosts() {
        return postRepository.findByDisableDateIsNotNull();
    }

    @Override
    public List<Post> getDeletedPostsByAuthor(Long authorId) {
        validateId(authorId, "Author ID");
        return postRepository.findByAuthorIdAndDisableDateIsNotNull(authorId);
    }

    @Override
    public void restorePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuleException("Post.not.found.with.ID"));

        if (post.getDisableDate() == null) {
            throw new RuleException("Post.is.not.deleted");
        }

        post.setDisableDate(null);
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void permanentDeletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuleException("Post.not.found.with.ID"));
        postRepository.delete(post);
    }

    // ==================== مدیریت وضعیت ====================

    @Override
    public void changePostStatus(Long id, PostStatus status) {
        validateStatus(status);
        Post post = getActivePostById(id);
        post.setStatus(status);

        if (status == PostStatus.PUBLISHED && post.getPublishedAt() == null) {
            post.setPublishedAt(LocalDateTime.now());
        }

        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);
    }

    @Override
    public void publishPost(Long id) {
        changePostStatus(id, PostStatus.PUBLISHED);
    }

    @Override
    public void unpublishPost(Long id) {
        changePostStatus(id, PostStatus.DRAFT);
    }

    // ==================== شمارش‌ها ====================

    @Override
    public long countAllPosts() {
        return postRepository.countByDisableDateIsNull();
    }

    @Override
    public long countPublishedPosts() {
        return postRepository.countByStatusAndDisableDateIsNull(PostStatus.PUBLISHED);
    }

    @Override
    public long countDraftPosts() {
        return postRepository.countByStatusAndDisableDateIsNull(PostStatus.DRAFT);
    }

    @Override
    public long countPostsByCategory(Long categoryId) {
        validateId(categoryId, "Category ID");
        return postRepository.countByCategoryIdAndDisableDateIsNull(categoryId);
    }

    @Override
    public long countPostsByAuthor(Long authorId) {
        validateId(authorId, "Author ID");
        return postRepository.countByAuthorIdAndDisableDateIsNull(authorId);
    }

    @Override
    public long countDeletedPosts() {
        return postRepository.countByDisableDateIsNotNull();
    }

    // ==================== پست‌های جدید ====================

    @Override
    public List<Post> getLatestPosts(int limit) {
        validateLimit(limit);
        List<Post> posts = postRepository.findByDisableDateIsNullOrderByCreatedAtDesc();
        return posts.stream().limit(limit).toList();
    }

    @Override
    public List<Post> getLatestPublishedPosts(int limit) {
        validateLimit(limit);
        List<Post> posts = postRepository.findByStatusAndDisableDateIsNullOrderByCreatedAtDesc(PostStatus.PUBLISHED);
        return posts.stream().limit(limit).toList();
    }

    @Override
    public List<Post> getLatestPostsByCategory(Long categoryId, int limit) {
        validateId(categoryId, "Category ID");
        validateLimit(limit);
        List<Post> posts = postRepository.findByCategoryIdAndDisableDateIsNullOrderByCreatedAtDesc(categoryId);
        return posts.stream().limit(limit).toList();
    }

    @Override
    public List<Post> getLatestPublishedPostsByCategory(Long categoryId, int limit) {
        validateId(categoryId, "Category ID");
        validateLimit(limit);
        List<Post> posts = postRepository.findByCategoryIdAndStatusAndDisableDateIsNull(categoryId, PostStatus.PUBLISHED);
        return posts.stream()
                .sorted((p1, p2) -> p2.getPublishedAt().compareTo(p1.getPublishedAt()))
                .limit(limit)
                .toList();
    }




    private Post getActivePostById(Long id) {
        return postRepository.findByIdAndDisableDateIsNull(id)
                .orElseThrow(() -> new RuleException("Post.not.found.with.ID"));
    }

    private void performSoftDelete(Post post) {
        post.setDisableDate(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);
    }

    private String generateSlug(String title) {
        return title.toLowerCase()
                .replaceAll("[^a-z0-9\\u0600-\\u06FF\\s]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }

    private void validatePost(Post post) {
        if (post == null) {
            throw new RuleException("Post.cannot.be.null");
        }
        if (post.getTitle() == null || post.getTitle().trim().isEmpty()) {
            throw new RuleException("Post.title.cannot.be.empty");
        }
        if (post.getContent() == null || post.getContent().trim().isEmpty()) {
            throw new RuleException("Post.content.cannot.be.empty");
        }
        if (post.getAuthor() == null || post.getAuthor().getId() == null) {
            throw new RuleException("Post.must.have.an.author");
        }
    }

    private void validateId(Long id, String fieldName) {
        if (id == null) {
            throw new RuleException("cannot.be.null");
        }
    }

    private void validateStatus(PostStatus status) {
        if (status == null) {
            throw new RuleException("Status.cannot.be.null");
        }
    }

    private void validateKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new RuleException("Search.keyword.cannot.be.empty");
        }
    }

    private void validateLimit(int limit) {
        if (limit <= 0) {
            throw new RuleException("Limit.must.be.greater.than.0");
        }
    }
}