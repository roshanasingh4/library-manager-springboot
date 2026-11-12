package com.roshan.library.service;

import com.roshan.library.dto.BookAuthorView;
import com.roshan.library.entity.Author;
import com.roshan.library.entity.Book;
import com.roshan.library.exception.DuplicateResourceException;
import com.roshan.library.exception.ResourceNotFoundException;
import com.roshan.library.repository.AuthorRepository;
import com.roshan.library.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock private BookRepository bookRepository;
    @Mock private AuthorRepository authorRepository;
    @InjectMocks private BookService bookService;

    private final Author bond = author(2L, "Ruskin Bond");

    @Test
    void createLinksTheAuthorAndSaves() {
        Book input = book("The Blue Umbrella", "978-8129115201");
        when(bookRepository.existsByIsbn("978-8129115201")).thenReturn(false);
        when(authorRepository.findById(2L)).thenReturn(Optional.of(bond));
        when(bookRepository.saveAndFlush(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        Book saved = bookService.create(input, 2L);

        assertThat(saved.getAuthor()).isSameAs(bond);
        verify(bookRepository).saveAndFlush(input);
    }

    @Test
    void createRejectsDuplicateIsbn() {
        when(bookRepository.existsByIsbn("978-0143039655")).thenReturn(true);

        assertThatThrownBy(() -> bookService.create(book("Copy", "978-0143039655"), 2L))
                .isInstanceOf(DuplicateResourceException.class)
                .extracting("field").isEqualTo("isbn");
        verify(bookRepository, never()).saveAndFlush(any());
    }

    @Test
    void createFailsForUnknownAuthor() {
        when(bookRepository.existsByIsbn(any())).thenReturn(false);
        when(authorRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.create(book("Lost", "978-0000000001"), 404L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createTranslatesDatabaseIntegrityViolation() {
        when(bookRepository.existsByIsbn(any())).thenReturn(false);
        when(authorRepository.findById(2L)).thenReturn(Optional.of(bond));
        when(bookRepository.saveAndFlush(any())).thenThrow(new DataIntegrityViolationException("uk_book_isbn"));

        assertThatThrownBy(() -> bookService.create(book("Race", "978-0000000002"), 2L))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void updateChangesFieldsAndAuthor() {
        Book existing = book("Old Title", "978-0143039648");
        existing.setId(7L);
        existing.setAuthor(author(1L, "R. K. Narayan"));
        when(bookRepository.findByIdWithAuthor(7L)).thenReturn(Optional.of(existing));
        when(bookRepository.existsByIsbnAndIdNot("978-0143039648", 7L)).thenReturn(false);
        when(authorRepository.findById(2L)).thenReturn(Optional.of(bond));
        when(bookRepository.saveAndFlush(existing)).thenReturn(existing);

        Book changes = new Book("New Title", "978-0143039648", "Classic", 1960, new BigDecimal("275.50"));
        Book result = bookService.update(7L, changes, 2L);

        assertThat(result.getTitle()).isEqualTo("New Title");
        assertThat(result.getGenre()).isEqualTo("Classic");
        assertThat(result.getPrice()).isEqualByComparingTo("275.50");
        assertThat(result.getAuthor()).isSameAs(bond);
    }

    @Test
    void updateRejectsIsbnOfAnotherBook() {
        Book existing = book("Mine", "978-0000000003");
        existing.setId(7L);
        when(bookRepository.findByIdWithAuthor(7L)).thenReturn(Optional.of(existing));
        when(bookRepository.existsByIsbnAndIdNot("978-0143039655", 7L)).thenReturn(true);

        assertThatThrownBy(() -> bookService.update(7L, book("Mine", "978-0143039655"), 2L))
                .isInstanceOf(DuplicateResourceException.class);
        verify(bookRepository, never()).saveAndFlush(any());
    }

    @Test
    void updateFailsForMissingBook() {
        when(bookRepository.findByIdWithAuthor(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.update(99L, book("X", "978-0000000004"), 2L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void blankGenreReturnsTheFullInnerJoin() {
        List<BookAuthorView> all = List.of(new BookAuthorView(1L, "T", "978-0000000005", "Fiction",
                2000, BigDecimal.TEN, 2L, "Ruskin Bond", "India"));
        when(bookRepository.findAllBooksWithAuthors()).thenReturn(all);

        assertThat(bookService.findWithAuthorsByGenre("  ")).isSameAs(all);
        verify(bookRepository, never()).findBooksWithAuthorsByGenre(any());
    }

    @Test
    void genreFilterUsesTheGenreQuery() {
        when(bookRepository.findBooksWithAuthorsByGenre("Classic")).thenReturn(List.of());

        assertThat(bookService.findWithAuthorsByGenre(" Classic ")).isEmpty();
        verify(bookRepository).findBooksWithAuthorsByGenre("Classic");
    }

    private static Author author(Long id, String name) {
        Author a = new Author(name, name.replace(" ", "").toLowerCase() + "@library.org", "India", 1934);
        a.setId(id);
        return a;
    }

    private static Book book(String title, String isbn) {
        return new Book(title, isbn, "Fiction", 1980, new BigDecimal("199.00"));
    }
}
