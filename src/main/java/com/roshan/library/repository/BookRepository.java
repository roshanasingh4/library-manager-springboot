package com.roshan.library.repository;

import com.roshan.library.dto.BookAuthorView;
import com.roshan.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);

    boolean existsByIsbnAndIdNot(String isbn, Long id);

    /**
     * Custom INNER JOIN between books and authors.
     * Each row combines book columns with the name and country of its author.
     */
    @Query("""
           SELECT new com.roshan.library.dto.BookAuthorView(
                  b.id, b.title, b.isbn, b.genre, b.publishedYear, b.price,
                  a.id, a.name, a.country)
           FROM Book b
           INNER JOIN b.author a
           ORDER BY a.name ASC, b.title ASC
           """)
    List<BookAuthorView> findAllBooksWithAuthors();

    /**
     * Same INNER JOIN, filtered by genre (case-insensitive).
     */
    @Query("""
           SELECT new com.roshan.library.dto.BookAuthorView(
                  b.id, b.title, b.isbn, b.genre, b.publishedYear, b.price,
                  a.id, a.name, a.country)
           FROM Book b
           INNER JOIN b.author a
           WHERE LOWER(b.genre) = LOWER(:genre)
           ORDER BY a.name ASC, b.title ASC
           """)
    List<BookAuthorView> findBooksWithAuthorsByGenre(@Param("genre") String genre);

    /**
     * Loads a book with its author in one statement (INNER JOIN FETCH),
     * used by the edit form so the lazy association is available.
     */
    @Query("SELECT b FROM Book b INNER JOIN FETCH b.author WHERE b.id = :id")
    Optional<Book> findByIdWithAuthor(@Param("id") Long id);

    @Query("SELECT DISTINCT b.genre FROM Book b ORDER BY b.genre")
    List<String> findDistinctGenres();
}
