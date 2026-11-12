package com.roshan.library.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Many books belong to one author (Many-to-One).
 * The ISBN is unique across the catalogue.
 */
@Entity
@Table(name = "books",
       uniqueConstraints = @UniqueConstraint(name = "uk_book_isbn", columnNames = "isbn"))
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title must be at most 150 characters")
    @Column(nullable = false, length = 150)
    private String title;

    @NotBlank(message = "ISBN is required")
    @Pattern(regexp = "^97[89]-\\d{10}$", message = "ISBN must look like 978-0123456789")
    @Column(nullable = false, length = 14)
    private String isbn;

    @NotBlank(message = "Genre is required")
    @Column(nullable = false, length = 40)
    private String genre;

    @NotNull(message = "Published year is required")
    @Min(value = 1450, message = "Published year must be 1450 or later")
    @Max(value = 2030, message = "Published year must be 2030 or earlier")
    @Column(name = "published_year", nullable = false)
    private Integer publishedYear;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    @Digits(integer = 6, fraction = 2, message = "Price can have at most 2 decimal places")
    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal price;

    @NotNull(message = "Select an author")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_book_author"))
    private Author author;

    public Book() {
    }

    public Book(String title, String isbn, String genre, Integer publishedYear, BigDecimal price) {
        this.title = title;
        this.isbn = isbn;
        this.genre = genre;
        this.publishedYear = publishedYear;
        this.price = price;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public Integer getPublishedYear() { return publishedYear; }
    public void setPublishedYear(Integer publishedYear) { this.publishedYear = publishedYear; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Author getAuthor() { return author; }
    public void setAuthor(Author author) { this.author = author; }
}
