package com.iteam.buget.core.alert;

import com.iteam.buget.core.budget.Budget;
import com.iteam.buget.core.budget.BudgetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlertScheduler {

    private final BudgetRepository budgetRepository;
    private final AlertService alertService;

    // Re-check all budgets every hour to catch time-based threshold changes
    @Scheduled(fixedRate = 3_600_000)
    public void checkAllBudgets() {
        log.info("Running scheduled budget alert check...");
        List<Budget> budgets = budgetRepository.findAll();
        budgets.forEach(alertService::checkBudgetAlerts);
        log.info("Alert check complete for {} budgets", budgets.size());
    }
}