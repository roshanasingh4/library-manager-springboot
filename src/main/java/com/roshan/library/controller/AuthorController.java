package com.roshan.library.controller;

import com.roshan.library.entity.Author;
import com.roshan.library.exception.DuplicateResourceException;
import com.roshan.library.service.AuthorService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    /** READ: list every author with the number of books written. */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("authors", authorService.findAllSummaries());
        model.addAttribute("activePage", "authors");
        return "authors/list";
    }

    /** CREATE (step 1): show an empty form. */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("author", new Author());
        return formView(model, false);
    }

    /** CREATE (step 2): validate and save the submitted form. */
    @PostMapping
    public String create(@Valid @ModelAttribute("author") Author author,
                         BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return formView(model, false);
        }
        try {
            Author saved = authorService.create(author);
            redirect.addFlashAttribute("successMessage", "Author \"" + saved.getName() + "\" was added.");
            return "redirect:/authors";
        } catch (DuplicateResourceException ex) {
            result.rejectValue(ex.getField(), "duplicate", ex.getMessage());
            return formView(model, false);
        }
    }

    /** UPDATE (step 1): show the form pre-filled with the current values. */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("author", authorService.findById(id));
        return formView(model, true);
    }

    /** UPDATE (step 2): validate and save the changes. */
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("author") Author author,
                         BindingResult result, Model model, RedirectAttributes redirect) {
        author.setId(id);
        if (result.hasErrors()) {
            return formView(model, true);
        }
        try {
            Author saved = authorService.update(id, author);
            redirect.addFlashAttribute("successMessage", "Author \"" + saved.getName() + "\" was updated.");
            return "redirect:/authors";
        } catch (DuplicateResourceException ex) {
            result.rejectValue(ex.getField(), "duplicate", ex.getMessage());
            return formView(model, true);
        }
    }

    private String formView(Model model, boolean editing) {
        model.addAttribute("editing", editing);
        model.addAttribute("activePage", "authors");
        return "authors/form";
    }
}
