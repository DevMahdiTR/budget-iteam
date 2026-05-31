package com.iteam.buget.core.dto.request;

import com.iteam.buget.core.budget.BudgetPeriod;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BudgetRequest {
    @NotBlank private String name;
    private String description;
    @NotNull @DecimalMin("0.01") private BigDecimal globalCeiling;
    @NotNull private BudgetPeriod period;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean shared;
}
