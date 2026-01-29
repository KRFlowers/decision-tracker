package com.krflowers.subtraction.controller;

/**
 * Keep It or Let It Go - Item Decision Tracker
 * Controller for managing items under consideration
 * @author K Flowers
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.krflowers.subtraction.model.Decision;
import com.krflowers.subtraction.model.SubtractionItem;
import com.krflowers.subtraction.repository.SubtractionRepository;


@Controller
public class SubtractionController {

	// Available categories for items
	private static final List<String> CATEGORIES = Arrays.asList(
		"Books", "Clothing", "Electronics", "Furniture", "Media", "Subscriptions", "Misc"
	);

	@Autowired
	private SubtractionRepository repository;

	@GetMapping("/add-subtraction")
	public String getSubtractionForm(Model model) {
		SubtractionItem item = new SubtractionItem();
		item.setDateAdded(LocalDate.now());
		model.addAttribute("subtractionRequest", item);
		model.addAttribute("categories", CATEGORIES);
		return "add-subtraction";
	}

	@GetMapping("/subtractions")
	public String getSubtractions(
			@RequestParam(required = false) String sort,
			@RequestParam(required = false) String decision,
			Model model) {

		List<SubtractionItem> subtractions;

		// Determine sorting
		boolean sortByPriority = "priority".equals(sort);
		boolean sortByDateAdded = "dateAdded".equals(sort);

		// Apply filters
		if (decision != null && !decision.isEmpty()) {
			Decision dec = Decision.valueOf(decision);
			if (sortByPriority) {
				subtractions = repository.findByDecisionOrderByPriorityAsc(dec);
			} else if (sortByDateAdded) {
				subtractions = repository.findByDecisionOrderByDateAddedDesc(dec);
			} else {
				subtractions = repository.findByDecision(dec);
			}
		} else if (sortByPriority) {
			subtractions = repository.findAllByOrderByPriorityAsc();
		} else if (sortByDateAdded) {
			subtractions = repository.findAllByOrderByDateAddedDesc();
		} else {
			subtractions = repository.findAll();
		}

		model.addAttribute("subtractions", subtractions);
		model.addAttribute("selectedDecision", decision);
		model.addAttribute("currentSort", sort);

		return "subtractions";
	}

	@GetMapping("/subtractions/{id}")
	public String getSubtraction(@PathVariable long id, Model model) {
		var subtraction = repository.findById(id).get();

		model.addAttribute("id", subtraction.id);
		model.addAttribute("itemName", subtraction.itemName);
		model.addAttribute("category", subtraction.category);
		model.addAttribute("dateAdded", subtraction.dateAdded);
		model.addAttribute("priority", subtraction.priority);
		model.addAttribute("decision", subtraction.decision);
		model.addAttribute("decisionDate", subtraction.decisionDate);

		return "subtraction";
	}

	@PostMapping("/subtractions")
	public String postSubtraction(@Valid @ModelAttribute("subtractionRequest") SubtractionItem subtraction, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("categories", CATEGORIES);
			return "add-subtraction";
		}

		// Ensure dateAdded is set
		if (subtraction.getDateAdded() == null) {
			subtraction.setDateAdded(LocalDate.now());
		}

		var createdSubtraction = repository.save(subtraction);
		return "redirect:/subtractions/" + createdSubtraction.id;
	}

	// Edit form
	@GetMapping("/subtractions/{id}/edit")
	public String getEditForm(@PathVariable long id, Model model) {
		var subtraction = repository.findById(id).get();
		model.addAttribute("subtractionRequest", subtraction);
		model.addAttribute("categories", CATEGORIES);
		model.addAttribute("isEdit", true);
		return "edit-subtraction";
	}

	// Update existing
	@PostMapping("/subtractions/{id}")
	public String updateSubtraction(@PathVariable long id, @Valid @ModelAttribute("subtractionRequest") SubtractionItem subtraction, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("categories", CATEGORIES);
			model.addAttribute("isEdit", true);
			return "edit-subtraction";
		}
		subtraction.setId(id);
		repository.save(subtraction);
		return "redirect:/subtractions/" + id;
	}

	// Import page
	@GetMapping("/import")
	public String getImportForm() {
		return "import";
	}

	// Handle CSV import
	@PostMapping("/import")
	public String importCsv(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
		if (file.isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "Please select a file to upload.");
			return "redirect:/import";
		}

		int imported = 0;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
			String line;
			boolean firstLine = true;

			while ((line = reader.readLine()) != null) {
				// Skip header row
				if (firstLine) {
					firstLine = false;
					continue;
				}

				// Parse CSV line
				String[] fields = line.split(",");
				if (fields.length >= 2) {
					SubtractionItem item = new SubtractionItem();
					item.setItemName(fields[0].trim());
					item.setCategory(fields[1].trim());

					// Optional review field (1=High, 2=Medium, 3=Low)
					if (fields.length >= 3) {
						try {
							item.setPriority(Integer.parseInt(fields[2].trim()));
						} catch (NumberFormatException e) {
							item.setPriority(2); // Default to Medium
						}
					} else {
						item.setPriority(2); // Default to Medium
					}

					// Set defaults
					item.setDateAdded(LocalDate.now());
					item.setDecision(Decision.PENDING);
					item.setDecisionDate(null);

					repository.save(item);
					imported++;
				}
			}

			redirectAttributes.addFlashAttribute("success", "Imported " + imported + " items successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Error importing file: " + e.getMessage());
		}

		return "redirect:/subtractions";
	}

	// Default redirect to the list page
	@GetMapping("/")
	public String home() {
		return "redirect:/subtractions";
	}
}
