package java_project.expensetracker.model;

import jakarta.persistence.*;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

@Entity
@Data
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String description;

    @NotNull
    @Positive
    private Double amount;

    @NotNull
    private LocalDate date;

    @NotNull
    private String category;

    @NotNull
    private String paymentMethod;

    private String notes;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Added for monthly statistics
    public String getMonth() {
        if (date != null) {
            return date.getMonth().toString() + " " + date.getYear();
        }
        return "";
    }
}
