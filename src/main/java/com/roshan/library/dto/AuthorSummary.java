package com.roshan.library.dto;

/**
 * Author row for the list page, with the number of books found through an INNER JOIN.
 */
public record AuthorSummary(Long id, String name, String email, String country,
                            Integer birthYear, Long bookCount) {

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getCountry() { return country; }
    public Integer getBirthYear() { return birthYear; }
    public Long getBookCount() { return bookCount; }
}
