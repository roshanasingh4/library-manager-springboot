package com.roshan.library.controller;

import com.roshan.library.dto.BookForm;
import com.roshan.library.entity.Book;
import com.roshan.library.exception.DuplicateResourceException;
import com.roshan.library.service.AuthorService;
import com.roshan.library.service.BookService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final AuthorService authorService;

    public BookController(BookService bookService, AuthorService authorService) {
        this.bookService = bookService;
        this.authorService = authorService;
    }

    /**
     * READ: the catalogue, produced by the custom INNER JOIN query
     * (books joined with their authors), optionally filtered by genre.
     */
    @GetMapping
    public String list(@RequestParam(required = false) String genre, Model model) {
        model.addAttribute("rows", bookService.findWithAuthorsByGenre(genre));
        model.addAttribute("genres", bookService.findGenres());
        model.addAttribute("selectedGenre", genre == null ? "" : genre);
        model.addAttribute("activePage", "books");
        return "books/list";
    }

    @GetMapping("/new")
    public String showCreateForm(@RequestParam(required = false) Long authorId, Model model) {
        BookForm form = new BookForm();
        form.setAuthorId(authorId);
        model.addAttribute("book", form);
        return formView(model, false);
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("book") BookForm form,
                         BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return formView(model, false);
        }
        try {
            Book saved = bookService.create(form.toEntity(), form.getAuthorId());
            redirect.addFlashAttribute("successMessage", "Book \"" + saved.getTitle() + "\" was added.");
            return "redirect:/books";
        } catch (DuplicateResourceException ex) {
            result.rejectValue(ex.getField(), "duplicate", ex.getMessage());
            return formView(model, false);
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("book", BookForm.fromEntity(bookService.findById(id)));
        return formView(model, true);
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("book") BookForm form,
                         BindingResult result, Model model, RedirectAttributes redirect) {
        form.setId(id);
        if (result.hasErrors()) {
            return formView(model, true);
        }
        try {
            Book saved = bookService.update(id, form.toEntity(), form.getAuthorId());
            redirect.addFlashAttribute("successMessage", "Book \"" + saved.getTitle() + "\" was updated.");
            return "redirect:/books";
        } catch (DuplicateResourceException ex) {
            result.rejectValue(ex.getField(), "duplicate", ex.getMessage());
            return formView(model, true);
        }
    }

    private String formView(Model model, boolean editing) {
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("editing", editing);
        model.addAttribute("activePage", "books");
        return "books/form";
    }
}
