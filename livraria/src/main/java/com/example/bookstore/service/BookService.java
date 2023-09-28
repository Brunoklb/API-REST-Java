package com.example.bookstore.service;

import com.example.bookstore.controller.BookResponse;
import com.example.bookstore.model.Book;
import com.example.bookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> getBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    public ResponseEntity<BookResponse> createBook(Book book) {
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            String message = "Já existe um livro com o ISBN " + book.getIsbn() + ".";
            BookResponse response = new BookResponse(book, message);
            return ResponseEntity.badRequest().body(response);
        }

        if (book.getStock() < 0) {
            String message = "O estoque não pode ser negativo.";
            BookResponse response = new BookResponse(book, message);
            return ResponseEntity.badRequest().body(response);
        }

        Book savedBook = bookRepository.save(book);
        String message = "Livro criado com sucesso.";
        BookResponse response = new BookResponse(savedBook, message);
        return ResponseEntity.created(null).body(response);
    }

    public boolean existsByIsbn(String isbn) {
        return bookRepository.existsByIsbn(isbn);
    }

    public ResponseEntity<String> deleteBook(Long id) {
        Optional<Book> bookToDeleteOptional = bookRepository.findById(id);

        if (bookToDeleteOptional.isPresent()) {
            Book bookToDelete = bookToDeleteOptional.get();

            if (bookToDelete.getStock() != null && bookToDelete.getStock() > 0) {
                return ResponseEntity.badRequest()
                        .body("Não é permitido excluir o livro, pois ainda há cópias em estoque.");
            } else {
                bookRepository.deleteById(id);
                return ResponseEntity.ok("Livro excluído com sucesso.");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<BookResponse> updateBook(Long id, Book updatedBook) {
        Optional<Book> existingBookOptional = bookRepository.findById(id);

        if (existingBookOptional.isPresent()) {
            Book existingBook = existingBookOptional.get();

            int newStock = updatedBook.getStock();
            if (newStock >= 0) {
                existingBook.setTitle(updatedBook.getTitle());
                existingBook.setAuthor(updatedBook.getAuthor());
                existingBook.setIsbn(updatedBook.getIsbn());
                existingBook.setPrice(updatedBook.getPrice());
                existingBook.setStock(newStock);

                int newUserRating = updatedBook.getUserRating();
                if (newUserRating >= 1 && newUserRating <= 5) {
                    existingBook.setUserRating(newUserRating);

                    double currentAverageRating = existingBook.getAverageRating();
                    int numberOfRatings = existingBook.getNumberOfRatings();

                    double newAverageRating = ((currentAverageRating * numberOfRatings) + newUserRating)
                            / (numberOfRatings + 1);
                    existingBook.setAverageRating(newAverageRating);
                    existingBook.setNumberOfRatings(numberOfRatings + 1);

                    Book updated = bookRepository.save(existingBook);

                    BookResponse response = new BookResponse(updated, "Livro atualizado com sucesso");
                    return ResponseEntity.ok(response);
                } else {
                    BookResponse response = new BookResponse(null, "Avaliação do usuário deve estar entre 1 e 5.");
                    return ResponseEntity.badRequest().body(response);
                }
            } else {
                BookResponse response = new BookResponse(null, "O valor do estoque não pode ser negativo.");
                return ResponseEntity.badRequest().body(response);
            }
        } else {
            BookResponse response = new BookResponse(null, "Livro não encontrado com o ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    public BookResponse addCopiesToStock(Long id, Integer quantity) {
        Optional<Book> bookOptional = bookRepository.findById(id);

        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            int currentStock = book.getStock();

            if (quantity > 0) {
                book.setStock(currentStock + quantity);
                bookRepository.save(book);
                String mensagem = "Adicionadas " + quantity + " cópias ao estoque do livro.";
                return new BookResponse(book, mensagem);
            } else {
                return new BookResponse(null, "A quantidade a ser adicionada deve ser maior que zero.");
            }
        } else {
            return new BookResponse(null, "Livro não encontrado.");
        }
    }


    public BookResponse removeCopiesFromStock(Long id, Integer quantity) {
        Optional<Book> bookOptional = bookRepository.findById(id);

        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            int currentStock = book.getStock();

            if (quantity > 0 && quantity <= currentStock) {
                book.setStock(currentStock - quantity);
                bookRepository.save(book);
                String message = "Removidas " + quantity + " cópias do estoque do livro.";
                return new BookResponse(book, message);
            } else if (quantity <= 0) {
                return new BookResponse(null, "A quantidade a ser removida deve ser maior que zero.");
            } else {
                return new BookResponse(null, "A quantidade a ser removida excede o estoque atual.");
            }
        } else {
            return new BookResponse(null, "Livro não encontrado.");
        }
    }
}
