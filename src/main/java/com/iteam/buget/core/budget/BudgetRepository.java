package com.iteam.buget.core.budget;

import com.iteam.buget.core.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByOwner(User owner);

    // All budgets where the user is either owner or member
    @Query("""
        SELECT DISTINCT b FROM Budget b
        LEFT JOIN b.members m
        WHERE b.owner = :user OR m.user = :user
    """)
    List<Budget> findAllForUser(User user);

    List<Budget> findBySharedTrue();
}