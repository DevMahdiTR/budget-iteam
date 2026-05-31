package com.iteam.buget.core.dto.response;

import com.iteam.buget.core.budget.BudgetPeriod;
import com.iteam.buget.core.budget.BudgetStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder
public class BudgetResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal globalCeiling;
    private BudgetPeriod period;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean shared;
    private BudgetStatus status;
    private LocalDateTime createdAt;
    private UserResponse owner;
    private List<BudgetMemberResponse> members;
    private List<CategoryLimitResponse> categoryLimits;
}
