package com.example.sinablog.cotroller;

import com.example.sinablog.Service.category.CategoryService;
import com.example.sinablog.customeExeption.RuleException;
import com.example.sinablog.dtos.Category.CategoryMapper;
import com.example.sinablog.dtos.Category.CategoryRequestDTO;
import com.example.sinablog.dtos.Category.CategoryResponseDTO;
import com.example.sinablog.model.Category;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@Validated
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    public CategoryController(CategoryService categoryService, CategoryMapper categoryMapper) {
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
    }



    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
    public ResponseEntity<CategoryResponseDTO> createCategory(
            @Valid @RequestBody CategoryRequestDTO categoryRequestDTO) {

        Category category = categoryMapper.toEntity(categoryRequestDTO);
        Category createdCategory = categoryService.createCategory(category);
        CategoryResponseDTO responseDTO = categoryMapper.toDTO(createdCategory);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }



    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new RuleException("Category.not.found.with.ID"));

        CategoryResponseDTO responseDTO = categoryMapper.toDTO(category);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<CategoryResponseDTO> getCategoryByName(@PathVariable String name) {
        Category category = categoryService.getCategoryByName(name)
                .orElseThrow(() -> new RuleException("Category.not.found.with.name"));

        CategoryResponseDTO responseDTO = categoryMapper.toDTO(category);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<CategoryResponseDTO> getCategoryBySlug(@PathVariable String slug) {
        Category category = categoryService.getCategoryBySlug(slug)
                .orElseThrow(() -> new RuleException("Category.not.found.with.slug"));

        CategoryResponseDTO responseDTO = categoryMapper.toDTO(category);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        List<CategoryResponseDTO> responseDTOs = categories.stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<CategoryResponseDTO>> getAllCategoriesPaged(
            @PageableDefault(size = 20) Pageable pageable) {

        Page<Category> categories = categoryService.getAllCategories(pageable);
        Page<CategoryResponseDTO> responseDTOs = categories.map(categoryMapper::toDTO);

        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CategoryResponseDTO>> searchCategories(
            @RequestParam String keyword) {

        List<Category> categories = categoryService.searchCategories(keyword);
        List<CategoryResponseDTO> responseDTOs = categories.stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDTOs);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequestDTO categoryRequestDTO) {

        Category category = categoryMapper.toEntity(categoryRequestDTO);
        Category updatedCategory = categoryService.updateCategory(id, category);
        CategoryResponseDTO responseDTO = categoryMapper.toDTO(updatedCategory);

        return ResponseEntity.ok(responseDTO);
    }



    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }



    @GetMapping("/deleted")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CategoryResponseDTO>> getDeletedCategories() {
        List<Category> categories = categoryService.getDeletedCategories();
        List<CategoryResponseDTO> responseDTOs = categories.stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDTOs);
    }

    @PostMapping("/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponseDTO> restoreCategory(@PathVariable Long id) {
        categoryService.restoreCategory(id);

        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new RuleException("Category.not.found.after.restoration"));

        CategoryResponseDTO responseDTO = categoryMapper.toDTO(category);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}/permanent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> permanentDeleteCategory(@PathVariable Long id) {
        categoryService.permanentDeleteCategory(id);
        return ResponseEntity.noContent().build();
    }



    @GetMapping("/count")
    public ResponseEntity<Long> countAllCategories() {
        long count = categoryService.countAllCategories();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/deleted")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> countDeletedCategories() {
        long count = categoryService.countDeletedCategories();
        return ResponseEntity.ok(count);
    }


    @GetMapping("/exists/name/{name}")
    public ResponseEntity<Boolean> existsByName(@PathVariable String name) {
        boolean exists = categoryService.existsByName(name);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists/slug/{slug}")
    public ResponseEntity<Boolean> existsBySlug(@PathVariable String slug) {
        boolean exists = categoryService.existsBySlug(slug);
        return ResponseEntity.ok(exists);
    }


    @ExceptionHandler(RuleException.class)
    public ResponseEntity<String> handleRuleException(RuleException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Access.denied.You.don't.have.permission.to.perform.this.action.");
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(
            org.springframework.web.bind.MethodArgumentNotValidException ex) {

        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }
}