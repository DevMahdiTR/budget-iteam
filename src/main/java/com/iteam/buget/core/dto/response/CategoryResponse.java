package com.iteam.buget.core.dto.response;

import lombok.*;

@Data @Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private String icon;
    private boolean isDefault;
}
