package com.ecommerce.ecommerce.product.service;

import com.ecommerce.ecommerce.common.exception.DuplicateResourceException;
import com.ecommerce.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce.product.dto.request.CategoryRequest;
import com.ecommerce.ecommerce.product.dto.response.CategoryResponse;
import com.ecommerce.ecommerce.product.entity.Category;
import com.ecommerce.ecommerce.product.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findByActiveTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Ana kategorileri (parent'ı olmayan) getirir
     */
    public List<CategoryResponse> getRootCategories() {
        return categoryRepository.findRootCategories().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }


    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kategori", "id", id));
        return toResponse(category);
    }


    public CategoryResponse getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Kategori", "slug", slug));
        return toResponse(category);
    }


    public List<CategoryResponse> getSubCategories(Long parentId) {
        return categoryRepository.findByParentIdAndActiveTrue(parentId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }


    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        // Slug kontrolü
        if (categoryRepository.existsBySlug(request.getSlug())) {
            throw new DuplicateResourceException("Kategori", "slug", request.getSlug());
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setSlug(request.getSlug());
        category.setActive(request.getActive() != null ? request.getActive() : true);

        // Parent kategori varsa set et (0 da null olarak kabul edilir - root kategori için)
        if (request.getParentId() != null && request.getParentId() != 0) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Üst kategori", "id", request.getParentId()));
            category.setParent(parent);
        }

        Category saved = categoryRepository.save(category);
        return toResponse(saved);
    }


    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kategori", "id", id));

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        // Slug değiştiriliyorsa kontrol eder
        if (!category.getSlug().equals(request.getSlug())) {
            if (categoryRepository.existsBySlug(request.getSlug())) {
                throw new DuplicateResourceException("Kategori", "slug", request.getSlug());
            }
            category.setSlug(request.getSlug());
        }

        if (request.getActive() != null) {
            category.setActive(request.getActive());
        }

        // Parent değiştirme (0 da null olarak kabul edilir - root kategori için)
        if (request.getParentId() != null && request.getParentId() != 0) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Üst kategori", "id", request.getParentId()));
            category.setParent(parent);
        } else if (request.getParentId() != null && request.getParentId() == 0) {
            category.setParent(null);
        }

        Category updated = categoryRepository.save(category);
        return toResponse(updated);
    }

    /**
     * Kategori siler (soft delete)
     */
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kategori", "id", id));
        category.setActive(false);
        categoryRepository.save(category);
    }

    /**
     * Entity'yi Response'a dönüştürür
     */
    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .slug(category.getSlug())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .parentName(category.getParent() != null ? category.getParent().getName() : null)
                .active(category.getActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
