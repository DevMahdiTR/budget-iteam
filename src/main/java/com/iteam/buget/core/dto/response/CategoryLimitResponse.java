package com.iteam.buget.core.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Data @Builder
public class CategoryLimitResponse {
    private Long id;
    private CategoryResponse category;
    private BigDecimal ceiling;
    private BigDecimal spent;
    private double percentageUsed;
}
