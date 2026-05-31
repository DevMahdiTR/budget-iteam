package com.iteam.buget.core.category;

import com.iteam.buget.core.category.Category;
import com.iteam.buget.core.category.CategoryRepository;
import com.iteam.buget.core.dto.request.CategoryRequest;
import com.iteam.buget.core.dto.response.CategoryResponse;
import com.iteam.buget.core.mapper.CategoryMapper;
import com.iteam.buget.core.user.User;
import com.iteam.buget.exception.AccessDeniedException;
import com.iteam.buget.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryResponse> getAllForUser(User user) {
        return categoryRepository.findAllAvailableForUser(user)
                .stream().map(categoryMapper::toResponse).toList();
    }

    public List<CategoryResponse> getDefaults() {
        return categoryRepository.findByIsDefaultTrue()
                .stream().map(categoryMapper::toResponse).toList();
    }

    @Transactional
    public CategoryResponse createCustom(User user, CategoryRequest request) {
        Category category = Category.builder()
                .name(request.getName())
                .icon(request.getIcon())
                .createdBy(user)
                .isDefault(false)
                .build();
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse update(User user, Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        // Only creator or admin can edit
        if (!category.isDefault() && !category.getCreatedBy().getId().equals(user.getId())) {
            throw new AccessDeniedException("You don't have permission to edit this category");
        }
        category.setName(request.getName());
        category.setIcon(request.getIcon());
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Transactional
    public void delete(User user, Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        if (category.isDefault()) {
            throw new AccessDeniedException("Cannot delete default categories");
        }
        if (!category.getCreatedBy().getId().equals(user.getId())) {
            throw new AccessDeniedException("You don't have permission to delete this category");
        }
        categoryRepository.delete(category);
    }

    // Admin only: create system default category
    @Transactional
    public CategoryResponse createDefault(CategoryRequest request) {
        Category category = Category.builder()
                .name(request.getName())
                .icon(request.getIcon())
                .isDefault(true)
                .build();
        return categoryMapper.toResponse(categoryRepository.save(category));
    }
}