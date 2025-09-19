package com.example.sinablog.cotroller;

import com.example.sinablog.Service.Tag.TagService;
import com.example.sinablog.customeExeption.RuleException;
import com.example.sinablog.model.Tag;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    // ایجاد تگ جدید (فقط ادمین)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Tag> createTag(@Valid @RequestBody Tag tag) {
        try {
            Tag createdTag = tagService.createTag(tag);
            return ResponseEntity.ok(createdTag);
        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Failed to create tag: " + e.getMessage());
        }
    }

    // دریافت همه تگ‌ها (برای همه کاربران)
    @GetMapping
    public ResponseEntity<List<Tag>> getAllTags() {
        try {
            List<Tag> tags = tagService.getAllTags();
            return ResponseEntity.ok(tags);
        } catch (Exception e) {
            throw new RuleException("Failed to get tags: " + e.getMessage());
        }
    }

    // دریافت تگ‌ها با صفحه‌بندی (برای همه کاربران)
    @GetMapping("/paged")
    public ResponseEntity<Page<Tag>> getAllTagsPaged(Pageable pageable) {
        try {
            Page<Tag> tags = tagService.getAllTags(pageable);
            return ResponseEntity.ok(tags);
        } catch (Exception e) {
            throw new RuleException("Failed to get tags: " + e.getMessage());
        }
    }

    // دریافت تگ بر اساس ID (برای همه کاربران)
    @GetMapping("/{id}")
    public ResponseEntity<Tag> getTagById(@PathVariable Long id) {
        try {
            Tag tag = tagService.getTagById(id)
                    .orElseThrow(() -> new RuleException("Tag not found with ID: " + id));
            return ResponseEntity.ok(tag);
        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Failed to get tag: " + e.getMessage());
        }
    }

    // دریافت تگ بر اساس نام (برای همه کاربران)
    @GetMapping("/name/{name}")
    public ResponseEntity<Tag> getTagByName(@PathVariable String name) {
        try {
            Tag tag = tagService.getTagByName(name)
                    .orElseThrow(() -> new RuleException("Tag not found with name: " + name));
            return ResponseEntity.ok(tag);
        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Failed to get tag: " + e.getMessage());
        }
    }

    // دریافت تگ بر اساس slug (برای همه کاربران)
    @GetMapping("/slug/{slug}")
    public ResponseEntity<Tag> getTagBySlug(@PathVariable String slug) {
        try {
            Tag tag = tagService.getTagBySlug(slug)
                    .orElseThrow(() -> new RuleException("Tag not found with slug: " + slug));
            return ResponseEntity.ok(tag);
        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Failed to get tag: " + e.getMessage());
        }
    }

    // به‌روزرسانی تگ (فقط ادمین)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Tag> updateTag(@PathVariable Long id, @Valid @RequestBody Tag tag) {
        try {
            Tag updatedTag = tagService.updateTag(id, tag);
            return ResponseEntity.ok(updatedTag);
        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Failed to update tag: " + e.getMessage());
        }
    }

    // حذف تگ (فقط ادمین)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteTag(@PathVariable Long id) {
        try {
            tagService.deleteTag(id);
            return ResponseEntity.ok("Tag deleted successfully");
        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Failed to delete tag: " + e.getMessage());
        }
    }

    // جستجوی تگ بر اساس نام (برای همه کاربران)
    @GetMapping("/search/name/{name}")
    public ResponseEntity<List<Tag>> searchTagsByName(@PathVariable String name) {
        try {
            List<Tag> tags = tagService.searchTagsByName(name);
            return ResponseEntity.ok(tags);
        } catch (Exception e) {
            throw new RuleException("Failed to search tags: " + e.getMessage());
        }
    }

    // جستجوی تگ بر اساس slug (برای همه کاربران)
    @GetMapping("/search/slug/{slug}")
    public ResponseEntity<List<Tag>> searchTagsBySlug(@PathVariable String slug) {
        try {
            List<Tag> tags = tagService.searchTagsBySlug(slug);
            return ResponseEntity.ok(tags);
        } catch (Exception e) {
            throw new RuleException("Failed to search tags: " + e.getMessage());
        }
    }


    // دریافت تگ‌های یک پست (برای همه کاربران)
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Tag>> getTagsByPostId(@PathVariable Long postId) {
        try {
            List<Tag> tags = tagService.getTagsByPostId(postId);
            return ResponseEntity.ok(tags);
        } catch (Exception e) {
            throw new RuleException("Failed to get tags for post: " + e.getMessage());
        }
    }

    // بررسی وجود تگ بر اساس نام (برای همه کاربران)
    @GetMapping("/exists/name/{name}")
    public ResponseEntity<Boolean> checkTagExistsByName(@PathVariable String name) {
        try {
            boolean exists = tagService.existsByName(name);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            throw new RuleException("Failed to check tag existence: " + e.getMessage());
        }
    }

    // بررسی وجود تگ بر اساس slug (برای همه کاربران)
    @GetMapping("/exists/slug/{slug}")
    public ResponseEntity<Boolean> checkTagExistsBySlug(@PathVariable String slug) {
        try {
            boolean exists = tagService.existsBySlug(slug);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            throw new RuleException("Failed to check tag existence: " + e.getMessage());
        }
    }

    // تعداد کل تگ‌ها (برای همه کاربران)
    @GetMapping("/count")
    public ResponseEntity<Long> getTagsCount() {
        try {
            long count = tagService.countTags();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            throw new RuleException("Failed to count tags: " + e.getMessage());
        }
    }

    // ایجاد یا دریافت تگ‌ها (فقط ادمین - برای استفاده در ایجاد/ویرایش پست)
    @PostMapping("/get-or-create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Tag>> getOrCreateTags(@RequestBody List<String> tagNames) {
        try {
            List<Tag> tags = tagService.getOrCreateTags(tagNames);
            return ResponseEntity.ok(tags);
        } catch (Exception e) {
            throw new RuleException("Failed to get or create tags: " + e.getMessage());
        }
    }
}
