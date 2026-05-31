package com.iteam.buget.core.alert;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByBudgetId(Long budgetId);
    List<Alert> findByBudgetIdAndReadFalse(Long budgetId);
}
