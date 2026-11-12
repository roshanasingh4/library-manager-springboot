package com.roshan.library.dto;

import com.roshan.library.entity.Book;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Form-backing object for the add / edit book pages.
 * The author is chosen from a drop-down, so the form carries only the author id.
 */
public class BookForm {

    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title must be at most 150 characters")
    private String title;

    @NotBlank(message = "ISBN is required")
    @Pattern(regexp = "^97[89]-\\d{10}$", message = "ISBN must look like 978-0123456789")
    private String isbn;

    @NotBlank(message = "Genre is required")
    private String genre;

    @NotNull(message = "Published year is required")
    @Min(value = 1450, message = "Published year must be 1450 or later")
    @Max(value = 2030, message = "Published year must be 2030 or earlier")
    private Integer publishedYear;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    @Digits(integer = 6, fraction = 2, message = "Price can have at most 2 decimal places")
    private BigDecimal price;

    @NotNull(message = "Select an author")
    private Long authorId;

    public static BookForm fromEntity(Book book) {
        BookForm form = new BookForm();
        form.setId(book.getId());
        form.setTitle(book.getTitle());
        form.setIsbn(book.getIsbn());
        form.setGenre(book.getGenre());
        form.setPublishedYear(book.getPublishedYear());
        form.setPrice(book.getPrice());
        form.setAuthorId(book.getAuthor() != null ? book.getAuthor().getId() : null);
        return form;
    }

    public Book toEntity() {
        Book book = new Book(title.trim(), isbn.trim(), genre.trim(), publishedYear, price);
        book.setId(id);
        return book;
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

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
}
