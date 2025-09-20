package com.example.sinablog.Service.categoryService;

import com.example.sinablog.Repository.CategoryRepository;
import com.example.sinablog.customeExeption.RuleException;
import com.example.sinablog.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // ==================== عملیات CRUD ====================

    /**
     * ایجاد دسته‌بندی جدید
     * بررسی اعتبار داده‌ها، تولید خودکار Slug در صورت نیاز و بررسی تکراری نبودن نام و Slug
     */
    @Override
    public Category createCategory(Category category) {
        validateCategory(category);

        // تولید خودکار Slug در صورتی که مقداردهی نشده باشد
        if (category.getSlug() == null || category.getSlug().trim().isEmpty()) {
            category.setSlug(generateSlug(category.getName()));
        }

        // بررسی عدم وجود دسته‌بندی فعال با همین نام
        if (categoryRepository.existsByNameAndDisableDateIsNull(category.getName())) {
            throw new RuleException("Category with name '" + category.getName() + "' already exists");
        }

        // بررسی عدم وجود دسته‌بندی فعال با همین Slug
        if (categoryRepository.existsBySlugAndDisableDateIsNull(category.getSlug())) {
            throw new RuleException("Category with slug '" + category.getSlug() + "' already exists");
        }

        return categoryRepository.save(category);
    }

    /**
     * به‌روزرسانی دسته‌بندی موجود
     * بررسی تغییرات نام و Slug برای جلوگیری از تکراری بودن
     */
    @Override
    public Category updateCategory(Long id, Category category) {
        Category existingCategory = getActiveCategoryById(id);

        // بررسی و به‌روزرسانی نام در صورت تغییر
        if (category.getName() != null && !category.getName().trim().isEmpty()) {
            if (!existingCategory.getName().equals(category.getName()) &&
                    categoryRepository.existsByNameAndIdNotAndDisableDateIsNull(category.getName(), id)) {
                throw new RuleException("Category with name already exists");
            }
            existingCategory.setName(category.getName());
        }

        // بررسی و به‌روزرسانی Slug در صورت تغییر
        if (category.getSlug() != null && !category.getSlug().trim().isEmpty()) {
            if (!existingCategory.getSlug().equals(category.getSlug()) &&
                    categoryRepository.existsBySlugAndIdNotAndDisableDateIsNull(category.getSlug(), id)) {
                throw new RuleException("Category with slug already exists");
            }
            existingCategory.setSlug(category.getSlug());
        }

        // به‌روزرسانی توضیحات در صورت ارسال
        if (category.getDescription() != null) {
            existingCategory.setDescription(category.getDescription());
        }

        existingCategory.setUpdatedAt(LocalDateTime.now());
        return categoryRepository.save(existingCategory);
    }

    /**
     * حذف نرم دسته‌بندی با تنظیم تاریخ غیرفعال‌سازی
     */
    @Override
    public void deleteCategory(Long id) {
        Category category = getActiveCategoryById(id);
        performSoftDelete(category);
    }

    /**
     * دریافت دسته‌بندی بر اساس شناسه (فقط دسته‌بندی‌های فعال)
     */
    @Override
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findByIdAndDisableDateIsNull(id);
    }

    /**
     * دریافت دسته‌بندی بر اساس نام (فقط دسته‌بندی‌های فعال)
     */
    @Override
    public Optional<Category> getCategoryByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new RuleException("Category name cannot be empty");
        }
        return categoryRepository.findByNameAndDisableDateIsNull(name);
    }

    /**
     * دریافت دسته‌بندی بر اساس Slug (فقط دسته‌بندی‌های فعال)
     */
    @Override
    public Optional<Category> getCategoryBySlug(String slug) {
        if (slug == null || slug.trim().isEmpty()) {
            throw new RuleException("Category slug cannot be empty");
        }
        return categoryRepository.findBySlugAndDisableDateIsNull(slug);
    }

    // ==================== دسته‌بندی‌های فعال ====================

    /**
     * دریافت تمام دسته‌بندی‌های فعال به صورت لیست مرتب‌شده
     */
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findByDisableDateIsNullOrderByNameAsc();
    }

    /**
     * دریافت دسته‌بندی‌های فعال به صورت صفحه‌بندی‌شده
     */
    @Override
    public Page<Category> getAllCategories(Pageable pageable) {
        return categoryRepository.findByDisableDateIsNull(pageable);
    }

    /**
     * جستجو در دسته‌بندی‌های فعال بر اساس کلمه کلیدی
     */
    @Override
    public List<Category> searchCategories(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new RuleException("Search keyword cannot be empty");
        }
        return categoryRepository.searchActiveCategories(keyword);
    }

    // ==================== دسته‌بندی‌های حذف شده ====================

    /**
     * دریافت لیست دسته‌بندی‌های حذف شده (غیرفعال)
     */
    @Override
    public List<Category> getDeletedCategories() {
        return categoryRepository.findByDisableDateIsNotNull();
    }

    /**
     * بازیابی دسته‌بندی حذف شده با حذف تاریخ غیرفعال‌سازی
     */
    @Override
    public void restoreCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuleException("Category not found with ID "));

        if (category.getDisableDate() == null) {
            throw new RuleException("Category is not deleted");
        }

        category.setDisableDate(null);
        category.setUpdatedAt(LocalDateTime.now());
        categoryRepository.save(category);
    }

    /**
     * حذف فیزیکی دسته‌بندی (فقط برای ادمین)
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void permanentDeleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuleException("Category not found with ID"));
        categoryRepository.delete(category);
    }

    // ==================== شمارش‌ها ====================

    /**
     * شمارش دسته‌بندی‌های فعال
     */
    @Override
    public long countAllCategories() {
        return categoryRepository.countByDisableDateIsNull();
    }

    /**
     * شمارش دسته‌بندی‌های حذف شده
     */
    @Override
    public long countDeletedCategories() {
        return categoryRepository.countByDisableDateIsNotNull();
    }

    // ==================== بررسی وجود ====================

    /**
     * بررسی وجود دسته‌بندی فعال با نام مشخص
     */
    @Override
    public boolean existsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new RuleException("Category name cannot be empty");
        }
        return categoryRepository.existsByNameAndDisableDateIsNull(name);
    }

    /**
     * بررسی وجود دسته‌بندی فعال با Slug مشخص
     */
    @Override
    public boolean existsBySlug(String slug) {
        if (slug == null || slug.trim().isEmpty()) {
            throw new RuleException("Category slug cannot be empty");
        }
        return categoryRepository.existsBySlugAndDisableDateIsNull(slug);
    }

    /**
     * بررسی وجود دسته‌بندی فعال با نام مشخص (به جز یک شناسه خاص)
     */
    @Override
    public boolean existsByNameAndIdNot(String name, Long id) {
        if (name == null || name.trim().isEmpty()) {
            throw new RuleException("Category name cannot be empty");
        }
        if (id == null) {
            throw new RuleException("Category ID cannot be null");
        }
        return categoryRepository.existsByNameAndIdNotAndDisableDateIsNull(name, id);
    }

    /**
     * بررسی وجود دسته‌بندی فعال با Slug مشخص (به جز یک شناسه خاص)
     */
    @Override
    public boolean existsBySlugAndIdNot(String slug, Long id) {
        if (slug == null || slug.trim().isEmpty()) {
            throw new RuleException("Category slug cannot be empty");
        }
        if (id == null) {
            throw new RuleException("Category ID cannot be null");
        }
        return categoryRepository.existsBySlugAndIdNotAndDisableDateIsNull(slug, id);
    }

    // ==================== متدهای کمکی ====================

    /**
     * دریافت دسته‌بندی فعال بر اساس شناسه یا خطا
     */
    private Category getActiveCategoryById(Long id) {
        return categoryRepository.findByIdAndDisableDateIsNull(id)
                .orElseThrow(() -> new RuleException("Category not found with ID: " + id));
    }

    /**
     * انجام softDelete با تنظیم تاریخ غیرفعال‌سازی
     */
    private void performSoftDelete(Category category) {
        category.setDisableDate(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        categoryRepository.save(category);
    }

    /**
     * تولید Slug از نام دسته‌بندی
     * حذف کاراکترهای غیرمجاز، جایگزینی فاصله با خط تیره و یکسان‌سازی خطوط تیره
     */
    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\u0600-\\u06FF\\s]", "") // حذف کاراکترهای غیرمجاز (انگلیسی و فارسی)
                .replaceAll("\\s+", "-") // جایگزینی فاصله با خط تیره
                .replaceAll("-+", "-") // ادغام خطوط تیره تکراری
                .trim();
    }

    /**
     * اعتبارسنجی موجودیت دسته‌بندی و نام آن
     */
    private void validateCategory(Category category) {
        if (category == null) {
            throw new RuleException("Category cannot be null");
        }
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new RuleException("Category name cannot be empty");
        }
    }
}