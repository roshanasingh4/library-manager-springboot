package com.roshan.library.controller;

import com.roshan.library.service.AuthorService;
import com.roshan.library.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final AuthorService authorService;
    private final BookService bookService;

    public HomeController(AuthorService authorService, BookService bookService) {
        this.authorService = authorService;
        this.bookService = bookService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("authorCount", authorService.findAll().size());
        model.addAttribute("bookCount", bookService.count());
        model.addAttribute("genreCount", bookService.findGenres().size());
        // INNER JOIN: authors who have at least one book, ranked by number of books
        model.addAttribute("topAuthors", authorService.findAuthorsWithBooks());
        model.addAttribute("activePage", "home");
        return "home";
    }
}
