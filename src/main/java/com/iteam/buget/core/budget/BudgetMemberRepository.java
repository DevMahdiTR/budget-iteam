package com.iteam.buget.core.budget;

import com.iteam.buget.core.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BudgetMemberRepository extends JpaRepository<BudgetMember, Long> {
    Optional<BudgetMember> findByBudgetIdAndUser(Long budgetId, User user);
    List<BudgetMember> findByBudgetId(Long budgetId);
    boolean existsByBudgetIdAndUser(Long budgetId, User user);
}