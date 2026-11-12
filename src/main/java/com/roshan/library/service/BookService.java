package com.roshan.library.service;

import com.roshan.library.dto.BookAuthorView;
import com.roshan.library.entity.Author;
import com.roshan.library.entity.Book;
import com.roshan.library.exception.DuplicateResourceException;
import com.roshan.library.exception.ResourceNotFoundException;
import com.roshan.library.repository.AuthorRepository;
import com.roshan.library.repository.BookRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    /** Read operation backed by the custom INNER JOIN query. */
    public List<BookAuthorView> findAllWithAuthors() {
        return bookRepository.findAllBooksWithAuthors();
    }

    public List<BookAuthorView> findWithAuthorsByGenre(String genre) {
        if (genre == null || genre.isBlank()) {
            return findAllWithAuthors();
        }
        return bookRepository.findBooksWithAuthorsByGenre(genre.trim());
    }

    public List<String> findGenres() {
        return bookRepository.findDistinctGenres();
    }

    public long count() {
        return bookRepository.count();
    }

    public Book findById(Long id) {
        return bookRepository.findByIdWithAuthor(id)
                .orElseThrow(() -> new ResourceNotFoundException("No book exists with id " + id));
    }

    @Transactional
    public Book create(Book book, Long authorId) {
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw duplicateIsbn(book.getIsbn());
        }
        book.setId(null);
        book.setAuthor(loadAuthor(authorId));
        return saveOrTranslate(book);
    }

    @Transactional
    public Book update(Long id, Book changes, Long authorId) {
        Book existing = findById(id);
        if (bookRepository.existsByIsbnAndIdNot(changes.getIsbn(), id)) {
            throw duplicateIsbn(changes.getIsbn());
        }
        existing.setTitle(changes.getTitle());
        existing.setIsbn(changes.getIsbn());
        existing.setGenre(changes.getGenre());
        existing.setPublishedYear(changes.getPublishedYear());
        existing.setPrice(changes.getPrice());
        existing.setAuthor(loadAuthor(authorId));
        return saveOrTranslate(existing);
    }

    private Author loadAuthor(Long authorId) {
        if (authorId == null) {
            throw new ResourceNotFoundException("An author must be selected");
        }
        return authorRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("No author exists with id " + authorId));
    }

    private Book saveOrTranslate(Book book) {
        try {
            return bookRepository.saveAndFlush(book);
        } catch (DataIntegrityViolationException ex) {
            throw duplicateIsbn(book.getIsbn());
        }
    }

    private DuplicateResourceException duplicateIsbn(String isbn) {
        return new DuplicateResourceException("isbn", "A book with the ISBN " + isbn + " already exists");
    }
}
