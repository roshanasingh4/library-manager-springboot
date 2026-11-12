package com.roshan.library.service;

import com.roshan.library.dto.AuthorSummary;
import com.roshan.library.entity.Author;
import com.roshan.library.exception.DuplicateResourceException;
import com.roshan.library.exception.ResourceNotFoundException;
import com.roshan.library.repository.AuthorRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public List<Author> findAll() {
        return authorRepository.findAllByOrderByNameAsc();
    }

    public List<AuthorSummary> findAllSummaries() {
        return authorRepository.findAllSummaries();
    }

    public List<AuthorSummary> findAuthorsWithBooks() {
        return authorRepository.findAuthorsWithBookCount();
    }

    public Author findById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No author exists with id " + id));
    }

    @Transactional
    public Author create(Author author) {
        normalise(author);
        if (authorRepository.existsByEmailIgnoreCase(author.getEmail())) {
            throw duplicateEmail(author.getEmail());
        }
        author.setId(null);
        return saveOrTranslate(author);
    }

    @Transactional
    public Author update(Long id, Author changes) {
        Author existing = findById(id);
        normalise(changes);
        if (authorRepository.existsByEmailIgnoreCaseAndIdNot(changes.getEmail(), id)) {
            throw duplicateEmail(changes.getEmail());
        }
        existing.setName(changes.getName());
        existing.setEmail(changes.getEmail());
        existing.setCountry(changes.getCountry());
        existing.setBirthYear(changes.getBirthYear());
        return saveOrTranslate(existing);
    }

    /**
     * saveAndFlush forces the INSERT/UPDATE to run now, so a unique-constraint
     * violation from the database (e.g. a concurrent insert) is caught here and
     * reported as a friendly DuplicateResourceException.
     */
    private Author saveOrTranslate(Author author) {
        try {
            return authorRepository.saveAndFlush(author);
        } catch (DataIntegrityViolationException ex) {
            throw duplicateEmail(author.getEmail());
        }
    }

    private void normalise(Author author) {
        author.setName(author.getName().trim());
        author.setEmail(author.getEmail().trim().toLowerCase());
        author.setCountry(author.getCountry().trim());
    }

    private DuplicateResourceException duplicateEmail(String email) {
        return new DuplicateResourceException("email",
                "An author with the e-mail " + email + " already exists");
    }
}
