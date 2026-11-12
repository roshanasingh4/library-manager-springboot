package com.roshan.library.dto;

import java.math.BigDecimal;

/**
 * One row of the INNER JOIN between books and authors.
 * Populated directly by a JPQL constructor expression in BookRepository.
 */
public record BookAuthorView(
        Long bookId,
        String title,
        String isbn,
        String genre,
        Integer publishedYear,
        BigDecimal price,
        Long authorId,
        String authorName,
        String authorCountry) {

    // Getter-style accessors so JSP Expression Language (e.g. ${row.title}) can read the fields.
    public Long getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getIsbn() { return isbn; }
    public String getGenre() { return genre; }
    public Integer getPublishedYear() { return publishedYear; }
    public BigDecimal getPrice() { return price; }
    public Long getAuthorId() { return authorId; }
    public String getAuthorName() { return authorName; }
    public String getAuthorCountry() { return authorCountry; }
}
