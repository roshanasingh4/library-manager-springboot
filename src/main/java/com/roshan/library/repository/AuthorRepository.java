package com.roshan.library.repository;

import com.roshan.library.dto.AuthorSummary;
import com.roshan.library.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    List<Author> findAllByOrderByNameAsc();

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    /**
     * Custom INNER JOIN query: only authors that have written at least one book,
     * together with how many books each has written.
     */
    @Query("""
           SELECT new com.roshan.library.dto.AuthorSummary(
                  a.id, a.name, a.email, a.country, a.birthYear, COUNT(b.id))
           FROM Author a
           INNER JOIN a.books b
           GROUP BY a.id, a.name, a.email, a.country, a.birthYear
           ORDER BY COUNT(b.id) DESC, a.name ASC
           """)
    List<AuthorSummary> findAuthorsWithBookCount();

    /**
     * All authors with their book count (LEFT JOIN so authors without books are still listed).
     */
    @Query("""
           SELECT new com.roshan.library.dto.AuthorSummary(
                  a.id, a.name, a.email, a.country, a.birthYear, COUNT(b.id))
           FROM Author a
           LEFT JOIN a.books b
           GROUP BY a.id, a.name, a.email, a.country, a.birthYear
           ORDER BY a.name ASC
           """)
    List<AuthorSummary> findAllSummaries();
}
