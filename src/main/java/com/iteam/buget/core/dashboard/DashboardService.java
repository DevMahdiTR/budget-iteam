package com.iteam.buget.core.dashboard;

import com.iteam.buget.core.alert.AlertService;
import com.iteam.buget.core.budget.Budget;
import com.iteam.buget.core.budget.BudgetCategoryLimit;
import com.iteam.buget.core.budget.BudgetCategoryLimitRepository;
import com.iteam.buget.core.budget.BudgetService;
import com.iteam.buget.core.dto.response.AlertResponse;
import com.iteam.buget.core.dto.response.CategoryLimitResponse;
import com.iteam.buget.core.dto.response.DashboardResponse;
import com.iteam.buget.core.mapper.BudgetMapper;
import com.iteam.buget.core.transaction.TransactionRepository;
import com.iteam.buget.core.transaction.TransactionType;
import com.iteam.buget.core.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TransactionRepository transactionRepository;
    private final BudgetCategoryLimitRepository limitRepository;
    private final BudgetService budgetService;
    private final AlertService alertService;
    private final BudgetMapper budgetMapper;

    public DashboardResponse getDashboard(User user, Long budgetId) {
        Budget budget = budgetService.findAndCheckAccess(user, budgetId);

        BigDecimal totalIncome = getOrZero(
                transactionRepository.sumByBudgetAndType(budgetId, TransactionType.INCOME));
        BigDecimal totalExpenses = getOrZero(
                transactionRepository.sumByBudgetAndType(budgetId, TransactionType.EXPENSE));
        BigDecimal balance = totalIncome.subtract(totalExpenses);

        double consumedPct = budget.getGlobalCeiling().compareTo(BigDecimal.ZERO) > 0
                ? totalExpenses.divide(budget.getGlobalCeiling(), 4, RoundingMode.HALF_UP).doubleValue() * 100 : 0;

        double savingsRate = totalIncome.compareTo(BigDecimal.ZERO) > 0
                ? balance.divide(totalIncome, 4, RoundingMode.HALF_UP).doubleValue() * 100 : 0;
        double expenseRate = totalIncome.compareTo(BigDecimal.ZERO) > 0
                ? totalExpenses.divide(totalIncome, 4, RoundingMode.HALF_UP).doubleValue() * 100 : 0;

        // Expense breakdown by category
        Map<String, BigDecimal> expenseByCategory = new LinkedHashMap<>();
        for (Object[] row : transactionRepository.expenseByCategory(budgetId)) {
            expenseByCategory.put((String) row[0], (BigDecimal) row[1]);
        }

        // Monthly trend
        Map<String, BigDecimal> monthlyTrend = new LinkedHashMap<>();
        for (Object[] row : transactionRepository.monthlyExpenseTrend(budgetId)) {
            String key = row[0] + "-" + String.format("%02d", ((Number) row[1]).intValue());
            monthlyTrend.put(key, (BigDecimal) row[2]);
        }

        // Category limit progress
        List<CategoryLimitResponse> limitProgress = new ArrayList<>();
        for (BudgetCategoryLimit limit : limitRepository.findByBudgetId(budgetId)) {
            BigDecimal spent = getOrZero(
                    transactionRepository.sumExpenseByBudgetAndCategory(budgetId, limit.getCategory().getId()));
            limitProgress.add(budgetMapper.toCategoryLimitResponse(limit, spent));
        }

        // Unread alerts
        List<AlertResponse> unreadAlerts = alertService.getUnreadAlerts(budgetId);

        return DashboardResponse.builder()
                .budgetId(budgetId)
                .budgetName(budget.getName())
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .balance(balance)
                .globalCeiling(budget.getGlobalCeiling())
                .budgetConsumedPercentage(consumedPct)
                .budgetStatus(budget.getStatus())
                .savingsRate(savingsRate)
                .expenseRate(expenseRate)
                .expenseByCategory(expenseByCategory)
                .monthlyTrend(monthlyTrend)
                .categoryLimitProgress(limitProgress)
                .unreadAlerts(unreadAlerts)
                .build();
    }

    private BigDecimal getOrZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}