package com.krflowers.subtraction.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.krflowers.subtraction.model.Decision;
import com.krflowers.subtraction.model.SubtractionItem;

/**
 * Repository for SubtractionItem entities
 * Provides sorting and filtering capabilities
 * @author K Flowers
 */
@Repository
public interface SubtractionRepository extends JpaRepository<SubtractionItem, Long> {

    // Find all, sorted by priority (1 = highest priority)
    List<SubtractionItem> findAllByOrderByPriorityAsc();

    // Find by decision
    List<SubtractionItem> findByDecision(Decision decision);

    // Find by decision, sorted by priority
    List<SubtractionItem> findByDecisionOrderByPriorityAsc(Decision decision);

    // Find by category
    List<SubtractionItem> findByCategory(String category);

    // Find by category, sorted by priority
    List<SubtractionItem> findByCategoryOrderByPriorityAsc(String category);

    // Find all, sorted by date added (newest first)
    List<SubtractionItem> findAllByOrderByDateAddedDesc();

    // Find by decision, sorted by date added (newest first)
    List<SubtractionItem> findByDecisionOrderByDateAddedDesc(Decision decision);
}
