package com.example.sinablog.cotroller;

import com.example.sinablog.Service.Post.PostService;
import com.example.sinablog.Service.User.UserService;
import com.example.sinablog.Service.category.CategoryService;
import com.example.sinablog.customeExeption.RuleException;
import com.example.sinablog.dtos.post.PostDto;
import com.example.sinablog.dtos.post.PostResponseDto;
import com.example.sinablog.model.Category;
import com.example.sinablog.model.Post;
import com.example.sinablog.model.User;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final CategoryService categoryService;

    public PostController(PostService postService, UserService userService, CategoryService categoryService) {
        this.postService = postService;
        this.userService = userService;
        this.categoryService = categoryService;
    }


    // ==================== ایجاد پست ====================

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('AUTHOR')")
    public ResponseEntity<PostResponseDto> createPost(@Valid @RequestBody PostDto postDto, Principal principal) {
        try {
            Post post = convertToEntity(postDto);

            // گرفتن کاربر لاگین شده
            String username = principal.getName();
            User author = userService.getUserByUsername(username).orElseThrow(()->new  RuleException("user.not.found"));
            post.setAuthor(author);

            Post createdPost = postService.createPost(post);
            PostResponseDto response = convertToResponseDto(createdPost);
            return ResponseEntity.ok(response);
        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Failed.to.create.post");
        }
    }

    // ==================== دریافت پست‌ها ====================

    @GetMapping
    public ResponseEntity<List<PostResponseDto>> getAllPosts() {
        try {
            List<Post> posts = postService.getAllPosts();
            List<PostResponseDto> response = posts.stream()
                    .map(this::convertToResponseDto)
                    .toList();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuleException("Failed.to.get.posts");
        }
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<PostResponseDto>> getAllPostsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        try {
            Pageable pageable = PageRequest.of(page, size,
                    Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
            Page<Post> posts = postService.getAllPosts(pageable);
            Page<PostResponseDto> response = posts.map(this::convertToResponseDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuleException("Failed.to.get.posts");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDto> getPostById(@PathVariable Long id) {
        try {
            Post post = postService.getPostById(id)
                    .orElseThrow(() -> new RuleException("Post.not.found.with.ID"));
            PostResponseDto response = convertToResponseDto(post);
            return ResponseEntity.ok(response);
        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Failed.to.get.post");
        }
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<PostResponseDto> getPostBySlug(@PathVariable String slug) {
        try {
            Post post = postService.getPostBySlug(slug)
                    .orElseThrow(() -> new RuleException("Post.not.found.with.slug"));
            PostResponseDto response = convertToResponseDto(post);
            return ResponseEntity.ok(response);
        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Failed.to.get.post");
        }
    }

    // ==================== پست‌های منتشر شده ====================

    @GetMapping("/published")
    public ResponseEntity<List<PostResponseDto>> getPublishedPosts() {
        try {
            List<Post> posts = postService.getPublishedPosts();
            List<PostResponseDto> response = posts.stream()
                    .map(this::convertToResponseDto)
                    .toList();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuleException("Failed.to.get.published.posts");
        }
    }

    @GetMapping("/published/paged")
    public ResponseEntity<Page<PostResponseDto>> getPublishedPostsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size,
                    Sort.by(Sort.Direction.DESC, "publishedAt"));
            Page<Post> posts = postService.getPublishedPosts(pageable);
            Page<PostResponseDto> response = posts.map(this::convertToResponseDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuleException("Failed.to.get.published.posts");
        }
    }

    // ==================== جستجو ====================

    @GetMapping("/search")
    public ResponseEntity<List<PostResponseDto>> searchPosts(
            @RequestParam String keyword) {
        try {
            List<Post> posts = postService.searchPosts(keyword);
            List<PostResponseDto> response = posts.stream()
                    .map(this::convertToResponseDto)
                    .toList();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuleException("Failed.to.search.posts");
        }
    }

    @GetMapping("/search/published")
    public ResponseEntity<List<PostResponseDto>> searchPublishedPosts(
            @RequestParam String keyword) {
        try {
            List<Post> posts = postService.searchPublishedPosts(keyword);
            List<PostResponseDto> response = posts.stream()
                    .map(this::convertToResponseDto)
                    .toList();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuleException("Failed.to.search.published.posts");
        }
    }

    // ==================== به‌روزرسانی پست ====================

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @postSecurityService.isPostAuthor(#id)")
    public ResponseEntity<PostResponseDto> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostDto postDto) {
        try {
            Post post = convertToEntity(postDto);
            Post updatedPost = postService.updatePost(id, post);
            PostResponseDto response = convertToResponseDto(updatedPost);
            return ResponseEntity.ok(response);
        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Failed.to.update.post");
        }
    }

    // ==================== مدیریت وضعیت ====================

    @PatchMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN') or @postSecurityService.isPostAuthor(#id)")
    public ResponseEntity<PostResponseDto> publishPost(@PathVariable Long id) {
        try {
            postService.publishPost(id);
            Post post = postService.getPostById(id)
                    .orElseThrow(() -> new RuleException("Post.not.found.with.ID"));
            PostResponseDto response = convertToResponseDto(post);
            return ResponseEntity.ok(response);
        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Failed.to.publish.post");
        }
    }

    @PatchMapping("/{id}/unpublish")
    @PreAuthorize("hasRole('ADMIN') or @postSecurityService.isPostAuthor(#id)")
    public ResponseEntity<PostResponseDto> unpublishPost(@PathVariable Long id) {
        try {
            postService.unpublishPost(id);
            Post post = postService.getPostById(id)
                    .orElseThrow(() -> new RuleException("Post.not.found.with.ID"));
            PostResponseDto response = convertToResponseDto(post);
            return ResponseEntity.ok(response);
        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Failed.to.unpublish.post");
        }
    }

    // ==================== حذف پست ====================

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @postSecurityService.isPostAuthor(#id)")
    public ResponseEntity<String> deletePost(@PathVariable Long id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.ok("Post.deleted.successfully");
        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Failed.to.delete.post");
        }
    }

    @DeleteMapping("/{id}/permanent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> permanentDeletePost(@PathVariable Long id) {
        try {
            postService.permanentDeletePost(id);
            return ResponseEntity.ok("Post.permanently.deleted");
        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Failed.to.permanently.delete.post");
        }
    }

    // ==================== بازیابی پست ====================

    @PostMapping("/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PostResponseDto> restorePost(@PathVariable Long id) {
        try {
            postService.restorePost(id);
            Post post = postService.getPostById(id)
                    .orElseThrow(() -> new RuleException("Post.not.found.with.ID"));
            PostResponseDto response = convertToResponseDto(post);
            return ResponseEntity.ok(response);
        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Failed.to.restore.post");
        }
    }


    // ==================== متدهای کمکی ====================

    private Post convertToEntity(PostDto postDto) {
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setSlug(postDto.getSlug());
        post.setContent(postDto.getContent());
        post.setExcerpt(postDto.getExcerpt());
        post.setStatus(postDto.getStatus());

        post.setTags(post.getTags());
        Category category= categoryService.getCategoryById(postDto.getCategoryId())
                .orElseThrow(() -> new RuleException("category.not.found"));

        post.setCategory(category);

        return post;
    }

    private PostResponseDto convertToResponseDto(Post post) {
        PostResponseDto response = new PostResponseDto();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setSlug(post.getSlug());
        response.setContent(post.getContent());
        response.setExcerpt(post.getExcerpt());
        response.setStatus(post.getStatus());
        response.setAuthorId(post.getAuthor() != null ? post.getAuthor().getId() : null);
        response.setAuthorName(post.getAuthor() != null ? post.getAuthor().getFullName() : null);
        response.setCategoryId(post.getCategory() != null ? post.getCategory().getId() : null);
        response.setCategoryName(post.getCategory() != null ? post.getCategory().getName() : null);

        response.setCreatedAt(post.getCreatedAt());
        response.setUpdatedAt(post.getUpdatedAt());
        response.setPublishedAt(post.getPublishedAt());
        response.setDisableDate(post.getDisableDate());

        // تنظیم tagIds و tagNames اگر tags وجود دارد
        if (post.getTags() != null) {
            response.setTagIds(post.getTags().stream().map(tag -> (long) tag.getId()).toList());
            response.setTagNames(post.getTags().stream().map(tag -> tag.getName()).toList());
        }

        return response;
    }
}
