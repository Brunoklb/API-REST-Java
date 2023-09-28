/*package com.example.bookstore.controller;

import com.example.bookstore.model.Book;
import com.example.bookstore.repository.BookRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class bookControllerTest {

    @InjectMocks
    private BookController BookController;

    @Mock
    private BookRepository bookRepository;

    @Test
    public void testGetAllBooks() {
        List<Book> livrosSimulados = new ArrayList<>();
        livrosSimulados.add(new Book(1L, "Book 1", "Autor 1", "ISBN 111", 20.0, 4.5, 100, 50, 0));
        livrosSimulados.add(new Book(2L, "Book 2", "Autor 2", "ISBN 222", 25.0, 4.0, 80, 30, 0));

        when(bookRepository.findAll()).thenReturn(livrosSimulados);

        ResponseEntity<List<Book>> response = BookController.getAllBooks();
        
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertFalse(response.getBody().isEmpty());

        assertEquals(livrosSimulados, response.getBody());
    }

    @Test
    public void testCreateBookWithNonUniqueISBN() {
        Book book = new Book(1L, "Book 1", "Autor 1", "ISBN 111", 20.0, 4.5, 100, 50, 0);

        when(bookRepository.existsByIsbn(book.getIsbn())).thenReturn(true);

        ResponseEntity<BookResponse> responseEntity = BookController.createBook(book);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        BookResponse response = responseEntity.getBody();
        assertEquals("Já existe um book com o ISBN: " + book.getIsbn(), response.getMessage());
    }

    @Test
    public void testUpdateBookWithNonExistingBook() {
        Long livroId = 1L;
        Book bookAtualizado = new Book(livroId, "Book Atualizado", "Autor Atualizado", "ISBN 111", 25.0, 4.0, 80, 30, 0);

        when(bookRepository.findById(livroId)).thenReturn(Optional.empty());

        ResponseEntity<BookResponse> responseEntity = BookController.updateBook(livroId, bookAtualizado);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

        BookResponse response = responseEntity.getBody();
        assertEquals("Book não encontrado com o ID: " + livroId, response.getMessage());
    }

    @Test
    public void testDeleteBookWithNonExistingBook() {
        Long livroId = 1L;

        when(bookRepository.findById(livroId)).thenReturn(Optional.empty());
        when(bookRepository.existsById(livroId)).thenReturn(false);

        ResponseEntity<String> responseEntity = BookController.deleteBook(livroId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

        assertEquals("Book não encontrado com o ID: " + livroId, responseEntity.getBody());
    }

    @Test
    public void testGetBooksByTitleWithMultipleResults() {
        // Simule uma lista de livros com o mesmo título
        String titulo = "Book";
        List<Book> livrosSimulados = new ArrayList<>();
        livrosSimulados.add(new Book(1L, "Book 1", "Autor 1", "ISBN 111", 20.0, 4.5, 100, 50, 0));
        livrosSimulados.add(new Book(2L, "Book 2", "Autor 2", "ISBN 222", 25.0, 4.0, 80, 30, 0));

        when(bookRepository.findByTittleContainingIgnoreCase(titulo)).thenReturn(livrosSimulados);

        ResponseEntity<List<Book>> responseEntity = BookController.getBooksByTitle(titulo);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        assertFalse(responseEntity.getBody().isEmpty());

        assertEquals(livrosSimulados, responseEntity.getBody());
    }

    @Test
    public void testDeleteBookWithStock() {
        Long livroId = 1L;
        Book bookComEstoque = new Book();
        bookComEstoque.setId(livroId);
        bookComEstoque.setStock(10);

        when(bookRepository.findById(livroId)).thenReturn(Optional.of(bookComEstoque));

        ResponseEntity<String> responseEntity = BookController.deleteBook(livroId);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        assertEquals("Não é permitido excluir o livro, pois ainda há cópias em estoque.", responseEntity.getBody());
    }

    @Test
    public void testAddNegativeCopiesToStock() {
        Long livroId = 1L;
        Book bookExistente = new Book();
        bookExistente.setId(livroId);
        bookExistente.setStock(10);

        when(bookRepository.findById(livroId)).thenReturn(Optional.of(bookExistente));

        ResponseEntity<String> responseEntity = BookController.addCopiesToStock(livroId, -10);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        assertEquals("A quantidade a ser adicionada deve ser maior que zero.", responseEntity.getBody());
    }

    @Test
    public void testUpdateBookWithInvalidUserRating() {
        Long livroId = 1L;

        Book bookExistente = new Book();
        bookExistente.setId(livroId);
        bookExistente.setUserRating(4);

        Book bookAtualizado = new Book();
        bookAtualizado.setUserRating(6);

        when(bookRepository.findById(livroId)).thenReturn(Optional.of(bookExistente));

        ResponseEntity<BookResponse> response = BookController.updateBook(livroId, bookAtualizado);

        assertEquals(400, response.getStatusCodeValue());

        assertTrue(response.getBody().getMessage().contains("Avaliação do usuário deve estar entre 1 e 5."));
    }

    @Test
    public void testGetBooksByTitle() {
        String titulo = "Book 1";

        List<Book> livrosSimulados = new ArrayList<>();
        livrosSimulados.add(new Book(1L, "Book 1", "Autor 1", "ISBN 111", 20.0, 4.5, 100, 50, 0));
        livrosSimulados.add(new Book(2L, "Book 1 Edição Especial", "Autor 2", "ISBN 222", 25.0, 4.0, 80, 30, 0));

        when(bookRepository.findByTittleContainingIgnoreCase(titulo)).thenReturn(livrosSimulados);

        ResponseEntity<List<Book>> response = BookController.getBooksByTitle(titulo);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertFalse(response.getBody().isEmpty());

        assertEquals(livrosSimulados, response.getBody());
    }
}*/