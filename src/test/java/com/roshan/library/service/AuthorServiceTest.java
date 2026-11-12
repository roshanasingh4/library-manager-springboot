package com.roshan.library.service;

import com.roshan.library.entity.Author;
import com.roshan.library.exception.DuplicateResourceException;
import com.roshan.library.exception.ResourceNotFoundException;
import com.roshan.library.repository.AuthorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock private AuthorRepository authorRepository;
    @InjectMocks private AuthorService authorService;

    @Test
    void createNormalisesAndSavesANewAuthor() {
        Author input = new Author("  Ruskin Bond ", " Ruskin.Bond@Library.org ", " India ", 1934);
        when(authorRepository.existsByEmailIgnoreCase("ruskin.bond@library.org")).thenReturn(false);
        when(authorRepository.saveAndFlush(any(Author.class))).thenAnswer(inv -> {
            Author a = inv.getArgument(0);
            a.setId(11L);
            return a;
        });

        Author saved = authorService.create(input);

        ArgumentCaptor<Author> captor = ArgumentCaptor.forClass(Author.class);
        verify(authorRepository).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Ruskin Bond");
        assertThat(captor.getValue().getEmail()).isEqualTo("ruskin.bond@library.org");
        assertThat(saved.getId()).isEqualTo(11L);
    }

    @Test
    void createRejectsDuplicateEmailBeforeSaving() {
        Author input = new Author("Copy", "taken@library.org", "India", 1950);
        when(authorRepository.existsByEmailIgnoreCase("taken@library.org")).thenReturn(true);

        assertThatThrownBy(() -> authorService.create(input))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("taken@library.org")
                .extracting("field").isEqualTo("email");
        verify(authorRepository, never()).saveAndFlush(any());
    }

    @Test
    void createTranslatesDatabaseIntegrityViolation() {
        Author input = new Author("Race", "race@library.org", "India", 1950);
        when(authorRepository.existsByEmailIgnoreCase(any())).thenReturn(false);
        when(authorRepository.saveAndFlush(any())).thenThrow(new DataIntegrityViolationException("uk_author_email"));

        assertThatThrownBy(() -> authorService.create(input))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void updateCopiesChangesOntoTheExistingAuthor() {
        Author existing = new Author("Old Name", "old@library.org", "India", 1900);
        existing.setId(5L);
        when(authorRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(authorRepository.existsByEmailIgnoreCaseAndIdNot("new@library.org", 5L)).thenReturn(false);
        when(authorRepository.saveAndFlush(existing)).thenReturn(existing);

        Author result = authorService.update(5L, new Author("New Name", "new@library.org", "Japan", 1949));

        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getEmail()).isEqualTo("new@library.org");
        assertThat(result.getCountry()).isEqualTo("Japan");
        assertThat(result.getBirthYear()).isEqualTo(1949);
    }

    @Test
    void updateRejectsEmailUsedByAnotherAuthor() {
        Author existing = new Author("Name", "mine@library.org", "India", 1900);
        existing.setId(5L);
        when(authorRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(authorRepository.existsByEmailIgnoreCaseAndIdNot("theirs@library.org", 5L)).thenReturn(true);

        assertThatThrownBy(() -> authorService.update(5L, new Author("Name", "theirs@library.org", "India", 1900)))
                .isInstanceOf(DuplicateResourceException.class);
        verify(authorRepository, never()).saveAndFlush(any());
    }

    @Test
    void findByIdThrowsWhenMissing() {
        when(authorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
