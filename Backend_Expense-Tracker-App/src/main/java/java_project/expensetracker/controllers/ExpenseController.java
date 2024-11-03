package java_project.expensetracker.controllers;

import java_project.expensetracker.model.Expense;
import java_project.expensetracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ExpenseController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @GetMapping("/")
    public String showDashboard(Model model) {
        // Get current month's data
        LocalDate startOfMonth = YearMonth.now().atDay(1);
        LocalDate endOfMonth = YearMonth.now().atEndOfMonth();
        
        List<Expense> monthlyExpenses = expenseRepository.findByDateBetween(startOfMonth, endOfMonth);
        Double monthlyTotal = expenseRepository.findTotalExpensesBetween(startOfMonth, endOfMonth);
        List<Object[]> categoryTotals = expenseRepository.findTotalsByCategory();
        
        model.addAttribute("expenses", monthlyExpenses);
        model.addAttribute("monthlyTotal", monthlyTotal != null ? monthlyTotal : 0.0);
        model.addAttribute("categoryTotals", convertToMap(categoryTotals));
        model.addAttribute("newExpense", new Expense());
        
        return "dashboard";
    }

    @PostMapping("/expense/add")
    public String addExpense(@ModelAttribute Expense expense) {
        if (expense.getDate() == null) {
            expense.setDate(LocalDate.now());
        }
        expenseRepository.save(expense);
        return "redirect:/";
    }

    @GetMapping("/expense/delete/{id}")
    public String deleteExpense(@PathVariable Long id) {
        expenseRepository.deleteById(id);
        return "redirect:/";
    }

    @GetMapping("/statistics")
    public String showStatistics(Model model) {
        // Add statistics data
        LocalDate startOfMonth = YearMonth.now().atDay(1);
        LocalDate endOfMonth = YearMonth.now().atEndOfMonth();
        
        Double monthlyTotal = expenseRepository.findTotalExpensesBetween(startOfMonth, endOfMonth);
        List<Object[]> categoryTotals = expenseRepository.findTotalsByCategory();
        
        model.addAttribute("monthlyTotal", monthlyTotal != null ? monthlyTotal : 0.0);
        model.addAttribute("categoryTotals", convertToMap(categoryTotals));
        
        return "statistics";
    }

    private Map<String, Double> convertToMap(List<Object[]> data) {
        Map<String, Double> map = new HashMap<>();
        if (data != null) {
            for (Object[] item : data) {
                map.put((String) item[0], (Double) item[1]);
            }
        }
        return map;
    }
}
