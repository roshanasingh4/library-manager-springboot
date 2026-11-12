package com.roshan.library.repository;

import com.roshan.library.dto.AuthorSummary;
import com.roshan.library.dto.BookAuthorView;
import com.roshan.library.entity.Author;
import com.roshan.library.entity.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Repository tests run against an in-memory H2 database created from the entities.
 */
@DataJpaTest
@ActiveProfiles("test")
class BookRepositoryTest {

    @Autowired private TestEntityManager em;
    @Autowired private BookRepository bookRepository;
    @Autowired private AuthorRepository authorRepository;

    private Author orwell;
    private Author austen;

    @BeforeEach
    void setUp() {
        orwell = em.persist(new Author("George Orwell", "orwell@test.org", "United Kingdom", 1903));
        austen = em.persist(new Author("Jane Austen", "austen@test.org", "United Kingdom", 1775));
        // An author without books: must be excluded by the INNER JOIN
        em.persist(new Author("New Writer", "new@test.org", "India", 1990));

        em.persist(book("Nineteen Eighty-Four", "978-0452284234", "Dystopian", orwell));
        em.persist(book("Animal Farm", "978-0452284241", "Dystopian", orwell));
        em.persist(book("Pride and Prejudice", "978-0141439518", "Classic", austen));
        em.flush();
        em.clear();
    }

    @Test
    void innerJoinReturnsEveryBookWithItsAuthor() {
        List<BookAuthorView> rows = bookRepository.findAllBooksWithAuthors();

        assertThat(rows).hasSize(3);
        // Ordered by author name, then title
        assertThat(rows).extracting(BookAuthorView::title)
                .containsExactly("Animal Farm", "Nineteen Eighty-Four", "Pride and Prejudice");
        assertThat(rows.get(0).authorName()).isEqualTo("George Orwell");
        assertThat(rows.get(2).authorName()).isEqualTo("Jane Austen");
    }

    @Test
    void innerJoinFiltersByGenreIgnoringCase() {
        List<BookAuthorView> rows = bookRepository.findBooksWithAuthorsByGenre("dystopian");

        assertThat(rows).hasSize(2)
                .allSatisfy(r -> assertThat(r.authorName()).isEqualTo("George Orwell"));
    }

    @Test
    void innerJoinOnAuthorsExcludesAuthorsWithoutBooks() {
        List<AuthorSummary> summaries = authorRepository.findAuthorsWithBookCount();

        assertThat(summaries).extracting(AuthorSummary::name)
                .containsExactly("George Orwell", "Jane Austen");
        assertThat(summaries.get(0).bookCount()).isEqualTo(2L);
    }

    @Test
    void leftJoinSummaryIncludesAuthorsWithoutBooks() {
        List<AuthorSummary> summaries = authorRepository.findAllSummaries();

        assertThat(summaries).hasSize(3);
        assertThat(summaries).filteredOn(s -> s.name().equals("New Writer"))
                .singleElement().extracting(AuthorSummary::bookCount).isEqualTo(0L);
    }

    @Test
    void findByIdWithAuthorFetchesTheAuthor() {
        Long id = bookRepository.findAll().get(0).getId();

        Book book = bookRepository.findByIdWithAuthor(id).orElseThrow();

        assertThat(book.getAuthor().getName()).isNotBlank();
    }

    @Test
    void existsByIsbnChecks() {
        Book book = bookRepository.findAll().stream()
                .filter(b -> b.getIsbn().equals("978-0141439518")).findFirst().orElseThrow();

        assertThat(bookRepository.existsByIsbn("978-0141439518")).isTrue();
        assertThat(bookRepository.existsByIsbn("978-0000000000")).isFalse();
        assertThat(bookRepository.existsByIsbnAndIdNot("978-0141439518", book.getId())).isFalse();
    }

    @Test
    void distinctGenresAreSorted() {
        assertThat(bookRepository.findDistinctGenres()).containsExactly("Classic", "Dystopian");
    }

    @Test
    void duplicateIsbnViolatesUniqueConstraint() {
        Book copy = book("Another Title", "978-0452284234", "Dystopian", em.find(Author.class, orwell.getId()));

        assertThatThrownBy(() -> bookRepository.saveAndFlush(copy))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void duplicateAuthorEmailViolatesUniqueConstraint() {
        Author clone = new Author("Someone Else", "orwell@test.org", "India", 1950);

        assertThat(authorRepository.existsByEmailIgnoreCase("ORWELL@test.org")).isTrue();
        assertThatThrownBy(() -> authorRepository.saveAndFlush(clone))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void bookWithoutAuthorIsRejected() {
        Book orphan = book("No Author", "978-1111111111", "Fiction", null);

        assertThatThrownBy(() -> bookRepository.saveAndFlush(orphan))
                .isInstanceOf(Exception.class);
    }

    private Book book(String title, String isbn, String genre, Author author) {
        Book b = new Book(title, isbn, genre, 1950, new BigDecimal("199.00"));
        b.setAuthor(author);
        return b;
    }
}
