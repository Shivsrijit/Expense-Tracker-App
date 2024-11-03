package java_project.expensetracker.repository;

import java_project.expensetracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByCategory(String category);
    
    List<Expense> findByDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.date BETWEEN ?1 AND ?2")
    Double findTotalExpensesBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT e.category, SUM(e.amount) FROM Expense e GROUP BY e.category")
    List<Object[]> findTotalsByCategory();
}