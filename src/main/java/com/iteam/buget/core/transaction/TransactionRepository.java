package com.iteam.buget.core.transaction;

import com.iteam.buget.core.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByBudgetId(Long budgetId);
    List<Transaction> findByBudgetIdAndType(Long budgetId, TransactionType type);
    List<Transaction> findByCreatedBy(User user);

    List<Transaction> findByBudgetIdAndTransactionDateBetween(
            Long budgetId, LocalDate from, LocalDate to);

    @Query("""
        SELECT SUM(t.amount) FROM Transaction t
        WHERE t.budget.id = :budgetId AND t.type = :type
    """)
    BigDecimal sumByBudgetAndType(@Param("budgetId") Long budgetId,
                                  @Param("type") TransactionType type);

    @Query("""
        SELECT SUM(t.amount) FROM Transaction t
        WHERE t.budget.id = :budgetId
          AND t.category.id = :categoryId
          AND t.type = 'EXPENSE'
    """)
    BigDecimal sumExpenseByBudgetAndCategory(@Param("budgetId") Long budgetId,
                                             @Param("categoryId") Long categoryId);

    // For monthly trend: group expenses by month
    @Query("""
        SELECT FUNCTION('YEAR', t.transactionDate)  AS yr,
               FUNCTION('MONTH', t.transactionDate) AS mo,
               SUM(t.amount) AS total
        FROM Transaction t
        WHERE t.budget.id = :budgetId AND t.type = 'EXPENSE'
        GROUP BY FUNCTION('YEAR', t.transactionDate),
                 FUNCTION('MONTH', t.transactionDate)
        ORDER BY yr, mo
    """)
    List<Object[]> monthlyExpenseTrend(@Param("budgetId") Long budgetId);

    // Expense breakdown by category
    @Query("""
        SELECT t.category.name, SUM(t.amount)
        FROM Transaction t
        WHERE t.budget.id = :budgetId AND t.type = 'EXPENSE'
        GROUP BY t.category.name
    """)
    List<Object[]> expenseByCategory(@Param("budgetId") Long budgetId);
}