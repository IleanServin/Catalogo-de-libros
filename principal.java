package com.example.bookcatalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BookCatalogApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookCatalogApplication.class, args);
    }
}
package com.example.bookcatalog.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String author;
    private String isbn;

    // Getters and Setters
}

package com.example.bookcatalog.repository;

import com.example.bookcatalog.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
package com.example.bookcatalog.service;

import com.example.bookcatalog.model.Book;
import com.example.bookcatalog.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}
package com.example.bookcatalog.controller;

import com.example.bookcatalog.model.Book;
import com.example.bookcatalog.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

        import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @PostMapping
    public Book saveBook(@RequestBody Book book) {
        return bookService.saveBook(book);
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }
}

package com.example.bookcatalog.dto;

public class BookDTO {

    private String title;
    private String author;
    private String isbn;

    // Getters and Setters
}

package com.example.bookcatalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
package com.example.bookcatalog.service;

import com.example.bookcatalog.dto.BookDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalBookService {

    @Autowired
    private RestTemplate restTemplate;

    private final String API_URL = "https://api.example.com/books";

    public BookDTO getBookFromExternalAPI(String isbn) {
        return restTemplate.getForObject(API_URL + "/" + isbn, BookDTO.class);
    }
}

package com.example.bookcatalog;

import com.example.bookcatalog.model.Book;
import com.example.bookcatalog.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class ConsoleRunner implements CommandLineRunner {

    @Autowired
    private BookService bookService;

    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. List all books");
            System.out.println("2. Find book by ID");
            System.out.println("3. Add a new book");
            System.out.println("4. Delete a book");
            System.out.println("5. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1:
                    bookService.getAllBooks().forEach(System.out::println);
                    break;
                case 2:
                    System.out.print("Enter book ID: ");
                    Long id = scanner.nextLong();
                    System.out.println(bookService.getBookById(id));
                    break;
                case 3:
                    System.out.print("Enter title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter author: ");
                    String author = scanner.nextLine();
                    System.out.print("Enter ISBN: ");
                    String isbn = scanner.nextLine();

                    Book book = new Book();
                    book.setTitle(title);
                    book.setAuthor(author);
                    book.setIsbn(isbn);
                    bookService.saveBook(book);
                    break;
                case 4:
                    System.out.print("Enter book ID: ");
                    Long deleteId = scanner.nextLong();
                    bookService.deleteBook(deleteId);
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }
}