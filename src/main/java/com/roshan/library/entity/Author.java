package com.roshan.library.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

/**
 * An author can write many books (One-to-Many).
 * The e-mail address is unique, which lets the application demonstrate
 * handling of data integrity violations on create and update.
 */
@Entity
@Table(name = "authors",
       uniqueConstraints = @UniqueConstraint(name = "uk_author_email", columnNames = "email"))
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "E-mail is required")
    @Email(message = "Enter a valid e-mail address")
    @Column(nullable = false, length = 120)
    private String email;

    @NotBlank(message = "Country is required")
    @Column(nullable = false, length = 60)
    private String country;

    @NotNull(message = "Birth year is required")
    @Min(value = 1500, message = "Birth year must be 1500 or later")
    @Max(value = 2015, message = "Birth year must be 2015 or earlier")
    @Column(name = "birth_year", nullable = false)
    private Integer birthYear;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    @OrderBy("title ASC")
    private List<Book> books = new ArrayList<>();

    public Author() {
    }

    public Author(String name, String email, String country, Integer birthYear) {
        this.name = name;
        this.email = email;
        this.country = country;
        this.birthYear = birthYear;
    }

    public void addBook(Book book) {
        books.add(book);
        book.setAuthor(this);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public Integer getBirthYear() { return birthYear; }
    public void setBirthYear(Integer birthYear) { this.birthYear = birthYear; }

    public List<Book> getBooks() { return books; }
    public void setBooks(List<Book> books) { this.books = books; }
}
