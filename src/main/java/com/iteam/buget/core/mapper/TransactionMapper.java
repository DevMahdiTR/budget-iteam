package com.iteam.buget.core.mapper;

import com.iteam.buget.core.dto.response.TransactionResponse;
import com.iteam.buget.core.transaction.Transaction;
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
