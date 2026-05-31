package com.iteam.buget.core.alert;

import com.iteam.buget.core.alert.Alert;
import com.iteam.buget.core.alert.AlertRepository;
import com.iteam.buget.core.budget.Budget;
import com.iteam.buget.core.budget.BudgetCategoryLimit;
import com.iteam.buget.core.budget.BudgetCategoryLimitRepository;
import com.iteam.buget.core.budget.BudgetStatus;
import com.iteam.buget.core.dto.response.AlertResponse;
import com.iteam.buget.core.mapper.AlertMapper;
import com.iteam.buget.core.transaction.TransactionRepository;
import com.iteam.buget.core.transaction.TransactionType;
import com.iteam.buget.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertService {

    private static final double WARNING_THRESHOLD = 0.80; // 80% = near limit

    private final AlertRepository alertRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetCategoryLimitRepository limitRepository;
    private final AlertMapper alertMapper;

    public List<AlertResponse> getAlertsForBudget(Long budgetId) {
        return alertRepository.findByBudgetId(budgetId)
                .stream().map(alertMapper::toResponse).toList();
    }

    public List<AlertResponse> getUnreadAlerts(Long budgetId) {
        return alertRepository.findByBudgetIdAndReadFalse(budgetId)
                .stream().map(alertMapper::toResponse).toList();
    }

    @Transactional
    public void markAsRead(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert", alertId));
        alert.setRead(true);
        alertRepository.save(alert);
    }

    @Transactional
    public void checkBudgetAlerts(Budget budget) {
        BigDecimal totalExpenses = getOrZero(
                transactionRepository.sumByBudgetAndType(budget.getId(), TransactionType.EXPENSE));

        double ratio = totalExpenses.divide(budget.getGlobalCeiling(), 4, RoundingMode.HALF_UP).doubleValue();

        // Update budget status
        BudgetStatus newStatus;
        if (ratio >= 1.0) {
            newStatus = BudgetStatus.EXCEEDED;
            createAlertIfNotExists(budget, null, AlertType.EXCEEDED,
                    "Budget \"" + budget.getName() + "\" has been exceeded! Spent: " + totalExpenses
                            + " / Ceiling: " + budget.getGlobalCeiling());
        } else if (ratio >= WARNING_THRESHOLD) {
            newStatus = BudgetStatus.NEAR_LIMIT;
            createAlertIfNotExists(budget, null, AlertType.THRESHOLD,
                    "Budget \"" + budget.getName() + "\" has reached " + Math.round(ratio * 100) + "% of its limit.");
        } else {
            newStatus = BudgetStatus.CONTROLLED;
        }
        budget.setStatus(newStatus);

        // Check per-category limits
        List<BudgetCategoryLimit> limits = limitRepository.findByBudgetId(budget.getId());
        for (BudgetCategoryLimit limit : limits) {
            BigDecimal categorySpent = getOrZero(
                    transactionRepository.sumExpenseByBudgetAndCategory(budget.getId(), limit.getCategory().getId()));
            double catRatio = categorySpent.divide(limit.getCeiling(), 4, RoundingMode.HALF_UP).doubleValue();

            if (catRatio >= 1.0) {
                createAlertIfNotExists(budget, limit, AlertType.EXCEEDED,
                        "Category \"" + limit.getCategory().getName() + "\" limit exceeded in budget \""
                                + budget.getName() + "\".");
            } else if (catRatio >= WARNING_THRESHOLD) {
                createAlertIfNotExists(budget, limit, AlertType.THRESHOLD,
                        "Category \"" + limit.getCategory().getName() + "\" has reached "
                                + Math.round(catRatio * 100) + "% of its limit.");
            }
        }
    }

    private void createAlertIfNotExists(Budget budget, BudgetCategoryLimit limit,
                                        AlertType type, String message) {
        // Avoid duplicate alerts of same type for same budget+category
        boolean exists = alertRepository.findByBudgetId(budget.getId()).stream().anyMatch(a ->
                a.getAlertType() == type
                        && !a.isRead()
                        && (limit == null ? a.getCategory() == null
                        : a.getCategory() != null && a.getCategory().getId().equals(limit.getCategory().getId()))
        );
        if (!exists) {
            Alert alert = Alert.builder()
                    .budget(budget)
                    .category(limit != null ? limit.getCategory() : null)
                    .alertType(type)
                    .message(message)
                    .read(false)
                    .build();
            alertRepository.save(alert);
        }
    }

    private BigDecimal getOrZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}