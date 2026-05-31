#!/usr/bin/env bash

cat > UserMapper.java << 'EOF'
package com.iteam.buget.mapper;

import com.iteam.buget.core.user.User;
import com.iteam.buget.dto.response.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toResponse(User user) {
        if (user == null) return null;
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().getRoleName().toString())
                .enabled(user.isEnabled())
                .accountValidated(user.isAccountValidated())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
EOF

cat > CategoryMapper.java << 'EOF'
package com.iteam.buget.mapper;

import com.iteam.buget.core.category.Category;
import com.iteam.buget.dto.response.CategoryResponse;
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
EOF

cat > BudgetMapper.java << 'EOF'
package com.iteam.buget.mapper;

import com.iteam.buget.core.budget.Budget;
import com.iteam.buget.core.budget.BudgetCategoryLimit;
import com.iteam.buget.core.budgetmember.BudgetMember;
import com.iteam.buget.dto.response.*;
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
EOF

cat > TransactionMapper.java << 'EOF'
package com.iteam.buget.mapper;

import com.iteam.buget.core.transaction.Transaction;
import com.iteam.buget.dto.response.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionMapper {

    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;

    public TransactionResponse toResponse(Transaction t) {
        return TransactionResponse.builder()
                .id(t.getId())
                .amount(t.getAmount())
                .type(t.getType())
                .transactionDate(t.getTransactionDate())
                .description(t.getDescription())
                .category(categoryMapper.toResponse(t.getCategory()))
                .budgetId(t.getBudget().getId())
                .budgetName(t.getBudget().getName())
                .createdBy(userMapper.toResponse(t.getCreatedBy()))
                .createdAt(t.getCreatedAt())
                .build();
    }
}
EOF

cat > CommentMapper.java << 'EOF'
package com.iteam.buget.mapper;

import com.iteam.buget.core.comment.Comment;
import com.iteam.buget.dto.response.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentMapper {
    private final UserMapper userMapper;

    public CommentResponse toResponse(Comment c) {
        return CommentResponse.builder()
                .id(c.getId())
                .content(c.getContent())
                .author(userMapper.toResponse(c.getAuthor()))
                .createdAt(c.getCreatedAt())
                .build();
    }
}
EOF

cat > AlertMapper.java << 'EOF'
package com.iteam.buget.mapper;

import com.iteam.buget.core.alert.Alert;
import com.iteam.buget.dto.response.AlertResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlertMapper {
    private final CategoryMapper categoryMapper;

    public AlertResponse toResponse(Alert a) {
        return AlertResponse.builder()
                .id(a.getId())
                .budgetId(a.getBudget().getId())
                .budgetName(a.getBudget().getName())
                .category(categoryMapper.toResponse(a.getCategory()))
                .alertType(a.getAlertType())
                .message(a.getMessage())
                .read(a.isRead())
                .triggeredAt(a.getTriggeredAt())
                .build();
    }
}
EOF

echo "Mappers done"