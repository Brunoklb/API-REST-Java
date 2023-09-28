package com.example.bookstore.controller;

import com.example.bookstore.model.Book;
import com.example.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<List<Book>> getBooksByTitle(@PathVariable String title) {
        List<Book> books = bookService.getBooksByTitle(title);

        if (books.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(books);
    }

    @PostMapping
    public ResponseEntity<BookResponse> createBook(@RequestBody Book book) {
        ResponseEntity<BookResponse> response = bookService.createBook(book);
        return response;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        ResponseEntity<String> response = bookService.deleteBook(id);
        return response;
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable Long id, @RequestBody Book updatedBook) {
        ResponseEntity<BookResponse> response = bookService.updateBook(id, updatedBook);
        return response;
    }

    @PostMapping("/{id}/add-copies/{quantity}")
    public ResponseEntity<BookResponse> addCopiesToStock(@PathVariable Long id, @PathVariable Integer quantity) {
        BookResponse response = bookService.addCopiesToStock(id, quantity);

        if (response.getBook() != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/remove-copies/{quantity}")
    public ResponseEntity<BookResponse> removeCopiesFromStock(@PathVariable Long id, @PathVariable Integer quantity) {
        BookResponse response = bookService.removeCopiesFromStock(id, quantity);

        if (response.getBook() != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
