package com.iteam.buget.core.dto.request;

import com.iteam.buget.core.transaction.TransactionType;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionRequest {
    @NotNull @DecimalMin("0.01") private BigDecimal amount;
    @NotNull private TransactionType type;
    @NotNull private LocalDate transactionDate;
    private String description;
    @NotNull private Long categoryId;
    @NotNull private Long budgetId;
}
