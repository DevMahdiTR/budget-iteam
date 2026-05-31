package com.iteam.buget.core.dto.response;


import com.iteam.buget.core.budget.BudgetStatus;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data @Builder
public class DashboardResponse {
    private Long budgetId;
    private String budgetName;
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal balance;
    private BigDecimal globalCeiling;
    private double budgetConsumedPercentage;
    private BudgetStatus budgetStatus;
    private double savingsRate;
    private double expenseRate;
    // category name -> amount spent
    private Map<String, BigDecimal> expenseByCategory;
    // "YYYY-MM" -> amount
    private Map<String, BigDecimal> monthlyTrend;
    private List<CategoryLimitResponse> categoryLimitProgress;
    private List<AlertResponse> unreadAlerts;
}
