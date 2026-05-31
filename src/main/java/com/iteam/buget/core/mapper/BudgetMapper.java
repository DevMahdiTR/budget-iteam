package com.iteam.buget.core.mapper;

import com.iteam.buget.core.budget.Budget;
import com.iteam.buget.core.budget.BudgetCategoryLimit;
import com.iteam.buget.core.budget.BudgetMember;
import com.iteam.buget.core.dto.response.BudgetMemberResponse;
import com.iteam.buget.core.dto.response.BudgetResponse;
import com.iteam.buget.core.dto.response.CategoryLimitResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BudgetMapper {

    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;

    public BudgetResponse toResponse(Budget b) {
        return BudgetResponse.builder()
                .id(b.getId())
                .name(b.getName())
                .description(b.getDescription())
                .globalCeiling(b.getGlobalCeiling())
                .period(b.getPeriod())
                .startDate(b.getStartDate())
                .endDate(b.getEndDate())
                .shared(b.isShared())
                .status(b.getStatus())
                .createdAt(b.getCreatedAt())
                .owner(userMapper.toResponse(b.getOwner()))
                .members(b.getMembers().stream().map(this::toMemberResponse).toList())
                .categoryLimits(b.getCategoryLimits().stream()
                        .map(l -> toCategoryLimitResponse(l, BigDecimal.ZERO)).toList())
                .build();
    }

    public BudgetMemberResponse toMemberResponse(BudgetMember m) {
        return BudgetMemberResponse.builder()
                .id(m.getId())
                .user(userMapper.toResponse(m.getUser()))
                .memberRole(m.getMemberRole())
                .joinedAt(m.getJoinedAt())
                .build();
    }

    public CategoryLimitResponse toCategoryLimitResponse(BudgetCategoryLimit limit, BigDecimal spent) {
        double pct = limit.getCeiling().compareTo(BigDecimal.ZERO) > 0
                ? spent.doubleValue() / limit.getCeiling().doubleValue() * 100 : 0;
        return CategoryLimitResponse.builder()
                .id(limit.getId())
                .category(categoryMapper.toResponse(limit.getCategory()))
                .ceiling(limit.getCeiling())
                .spent(spent)
                .percentageUsed(pct)
                .build();
    }
}
