package com.roshan.library.config;

import com.roshan.library.entity.Author;
import com.roshan.library.entity.Book;
import com.roshan.library.repository.AuthorRepository;
import com.roshan.library.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Populates both tables with 10 sample rows each when the application starts.
 * Author 10 is intentionally left without a book so the difference between the
 * INNER JOIN (home page) and the LEFT JOIN (authors page) is visible.
 */
@Component
@Profile("!test")
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public DataSeeder(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (authorRepository.count() > 0) {
            return;
        }

        List<Author> authors = authorRepository.saveAll(List.of(
                new Author("R. K. Narayan", "rk.narayan@library.org", "India", 1906),
                new Author("Ruskin Bond", "ruskin.bond@library.org", "India", 1934),
                new Author("Arundhati Roy", "arundhati.roy@library.org", "India", 1961),
                new Author("Chinua Achebe", "chinua.achebe@library.org", "Nigeria", 1930),
                new Author("Jane Austen", "jane.austen@library.org", "United Kingdom", 1775),
                new Author("George Orwell", "george.orwell@library.org", "United Kingdom", 1903),
                new Author("Gabriel Garcia Marquez", "gabriel.marquez@library.org", "Colombia", 1927),
                new Author("Haruki Murakami", "haruki.murakami@library.org", "Japan", 1949),
                new Author("Toni Morrison", "toni.morrison@library.org", "United States", 1931),
                new Author("Amitav Ghosh", "amitav.ghosh@library.org", "India", 1956)));

        Author narayan = authors.get(0), bond = authors.get(1), roy = authors.get(2),
               achebe = authors.get(3), austen = authors.get(4), orwell = authors.get(5),
               marquez = authors.get(6), murakami = authors.get(7), morrison = authors.get(8);

        bookRepository.saveAll(List.of(
                book("Malgudi Days", "978-0143039655", "Short Stories", 1943, "299.00", narayan),
                book("The Guide", "978-0143039648", "Fiction", 1958, "250.00", narayan),
                book("The Room on the Roof", "978-0143333388", "Fiction", 1956, "199.00", bond),
                book("The God of Small Things", "978-0679457312", "Fiction", 1997, "399.00", roy),
                book("Things Fall Apart", "978-0385474542", "Classic", 1958, "350.00", achebe),
                book("Pride and Prejudice", "978-0141439518", "Classic", 1813, "225.00", austen),
                book("Nineteen Eighty-Four", "978-0452284234", "Dystopian", 1949, "299.00", orwell),
                book("One Hundred Years of Solitude", "978-0060883287", "Magical Realism", 1967, "499.00", marquez),
                book("Kafka on the Shore", "978-1400079278", "Magical Realism", 2002, "450.00", murakami),
                book("Beloved", "978-1400033416", "Fiction", 1987, "375.00", morrison)));

        log.info("Seeded {} authors and {} books", authorRepository.count(), bookRepository.count());
    }

    private Book book(String title, String isbn, String genre, int year, String price, Author author) {
        Book b = new Book(title, isbn, genre, year, new BigDecimal(price));
        b.setAuthor(author);
        return b;
    }
}
