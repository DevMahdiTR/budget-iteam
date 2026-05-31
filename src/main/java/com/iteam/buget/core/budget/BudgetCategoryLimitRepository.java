package com.iteam.buget.core.budget;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BudgetCategoryLimitRepository extends JpaRepository<BudgetCategoryLimit, Long> {
    List<BudgetCategoryLimit> findByBudgetId(Long budgetId);
    Optional<BudgetCategoryLimit> findByBudgetIdAndCategoryId(Long budgetId, Long categoryId);
}