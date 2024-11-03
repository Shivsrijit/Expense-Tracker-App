//Shivsrijit Verma
package java_project.expensetracker.controllers;

import java_project.expensetracker.model.Expense;
import java_project.expensetracker.repository.ExpenseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ExpenseController {
    private static final Logger logger = LoggerFactory.getLogger(ExpenseController.class);

    @Autowired
    private ExpenseRepository expenseRepository;

    @GetMapping("/")
    public String showDashboard(Model model) {
        try {
            
            LocalDate startOfMonth = YearMonth.now().atDay(1);
            LocalDate endOfMonth = YearMonth.now().atEndOfMonth();
            
            List<Expense> monthlyExpenses = expenseRepository.findByDateBetween(startOfMonth, endOfMonth);
            Double monthlyTotal = expenseRepository.findTotalExpensesBetween(startOfMonth, endOfMonth);
            List<Object[]> categoryTotals = expenseRepository.findTotalsByCategory();
            
        
            Expense newExpense = new Expense();
            newExpense.setDate(LocalDate.now());
            newExpense.setPaymentMethod("Default");
            
            model.addAttribute("expenses", monthlyExpenses);
            model.addAttribute("monthlyTotal", monthlyTotal != null ? monthlyTotal : 0.0);
            model.addAttribute("categoryTotals", convertToMap(categoryTotals));
            model.addAttribute("newExpense", newExpense);
            
            logger.info("Dashboard loaded with {} expenses", monthlyExpenses.size());
        } catch (Exception e) {
            logger.error("Error loading dashboard", e);
            model.addAttribute("error", "Error loading dashboard: " + e.getMessage());
        }
        return "dashboard";
    }

    @PostMapping("/expense/add")
    public String addExpense(@ModelAttribute Expense expense, RedirectAttributes redirectAttrs) {
        try {
            logger.info("Received expense: {}", expense);
            if (expense.getDate() == null) {
                expense.setDate(LocalDate.now());
            }
            if (expense.getPaymentMethod() == null) {
                expense.setPaymentMethod("Default");
            }
            Expense savedExpense = expenseRepository.save(expense);
            logger.info("Saved expense: {}", savedExpense);
            redirectAttrs.addFlashAttribute("success", "Expense added successfully!");
        } catch (Exception e) {
            logger.error("Error adding expense", e);
            redirectAttrs.addFlashAttribute("error", "Error adding expense: " + e.getMessage());
        }
        return "redirect:/";
    }

    @GetMapping("/expense/delete/{id}")
    public String deleteExpense(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            logger.info("Attempting to delete expense with ID: {}", id);
            expenseRepository.deleteById(id);
            redirectAttrs.addFlashAttribute("success", "Expense deleted successfully!");
            logger.info("Expense deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting expense with ID: " + id, e);
            redirectAttrs.addFlashAttribute("error", "Error deleting expense: " + e.getMessage());
        }
        return "redirect:/";
    }

    @GetMapping("/statistics")
    public String showStatistics(Model model) {
        try {
            LocalDate startOfMonth = YearMonth.now().atDay(1);
            LocalDate endOfMonth = YearMonth.now().atEndOfMonth();
            
            Double monthlyTotal = expenseRepository.findTotalExpensesBetween(startOfMonth, endOfMonth);
            List<Object[]> categoryTotals = expenseRepository.findTotalsByCategory();
            Map<String, Double> categoryMap = convertToMap(categoryTotals);
            
            
            Map<String, Double> categoryPercentages = new HashMap<>();
            if (monthlyTotal != null && monthlyTotal > 0) {
                categoryMap.forEach((category, amount) -> 
                    categoryPercentages.put(category, (amount / monthlyTotal) * 100));
            }
            
            model.addAttribute("monthlyTotal", monthlyTotal != null ? monthlyTotal : 0.0);
            model.addAttribute("categoryTotals", categoryMap);
            model.addAttribute("categoryPercentages", categoryPercentages);
            
            logger.info("Statistics page loaded successfully");
        } catch (Exception e) {
            logger.error("Error loading statistics", e);
            model.addAttribute("error", "Error loading statistics: " + e.getMessage());
        }
        return "statistics";
    }

    private Map<String, Double> convertToMap(List<Object[]> data) {
        Map<String, Double> map = new HashMap<>();
        if (data != null) {
            for (Object[] item : data) {
                if (item[0] != null && item[1] != null) {
                    map.put((String) item[0], (Double) item[1]);
                }
            }
        }
        return map;
    }
}
