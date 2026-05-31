package com.iteam.buget.core.mapper;

import com.iteam.buget.core.category.Category;
import com.iteam.buget.core.dto.response.CategoryResponse;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategoryResponse toResponse(Category c) {
        if (c == null) return null;
        return CategoryResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .icon(c.getIcon())
                .isDefault(c.isDefault())
                .build();
    }
}
