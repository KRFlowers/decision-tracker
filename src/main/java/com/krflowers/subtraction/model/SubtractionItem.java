package com.krflowers.subtraction.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * SubtractionItem - Represents an item under consideration for keeping or letting go
 * @author K Flowers
 */
@Entity
public class SubtractionItem {

    public SubtractionItem() {}

    public SubtractionItem(long id, String itemName, String category,
                          LocalDate dateAdded, Integer priority,
                          Decision decision, LocalDate decisionDate) {
        this.id = id;
        this.itemName = itemName;
        this.category = category;
        this.dateAdded = dateAdded;
        this.priority = priority;
        this.decision = decision;
        this.decisionDate = decisionDate;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @NotBlank(message = "Please enter an item name.")
    @Size(max = 100, message = "The item name may not be more than 100 characters.")
    public String itemName;

    @NotBlank(message = "Please select a category.")
    public String category;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    public LocalDate dateAdded;

    @Min(value = 1, message = "Review must be High (1), Medium (2), or Low (3).")
    @Max(value = 3, message = "Review must be High (1), Medium (2), or Low (3).")
    public Integer priority;

    @Enumerated(EnumType.STRING)
    public Decision decision = Decision.PENDING;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    public LocalDate decisionDate;

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDate dateAdded) {
        this.dateAdded = dateAdded;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Decision getDecision() {
        return decision;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }

    public LocalDate getDecisionDate() {
        return decisionDate;
    }

    public void setDecisionDate(LocalDate decisionDate) {
        this.decisionDate = decisionDate;
    }
}
