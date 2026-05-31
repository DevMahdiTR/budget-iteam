package com.iteam.buget.core.dto.response;



import com.iteam.buget.core.transaction.TransactionType;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder
public class TransactionResponse {
    private Long id;
    private BigDecimal amount;
    private TransactionType type;
    private LocalDate transactionDate;
    private String description;
    private CategoryResponse category;
    private Long budgetId;
    private String budgetName;
    private UserResponse createdBy;
    private LocalDateTime createdAt;
}
