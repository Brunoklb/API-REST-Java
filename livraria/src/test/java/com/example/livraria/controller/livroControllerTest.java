package com.example.livraria.controller;

import com.example.livraria.model.Livro;
import com.example.livraria.repository.LivroRepository;
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
public class livroControllerTest {

    @InjectMocks
    private livroController livroController;

    @Mock
    private LivroRepository livroRepository;

    @Test
    public void testGetAllBooks() {
        List<Livro> livrosSimulados = new ArrayList<>();
        livrosSimulados.add(new Livro(1L, "Livro 1", "Autor 1", "ISBN 111", 20.0, 4.5, 100, 50, 0));
        livrosSimulados.add(new Livro(2L, "Livro 2", "Autor 2", "ISBN 222", 25.0, 4.0, 80, 30, 0));

        when(livroRepository.findAll()).thenReturn(livrosSimulados);

        ResponseEntity<List<Livro>> response = livroController.getAllBooks();
        
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertFalse(response.getBody().isEmpty());

        assertEquals(livrosSimulados, response.getBody());
    }

    @Test
    public void testCreateBookWithNonUniqueISBN() {
        Livro livro = new Livro(1L, "Livro 1", "Autor 1", "ISBN 111", 20.0, 4.5, 100, 50, 0);

        when(livroRepository.existsByIsbn(livro.getIsbn())).thenReturn(true);

        ResponseEntity<LivroResponse> responseEntity = livroController.createBook(livro);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        LivroResponse response = responseEntity.getBody();
        assertEquals("Já existe um livro com o ISBN: " + livro.getIsbn(), response.getMessage());
    }

    @Test
    public void testUpdateBookWithNonExistingBook() {
        Long livroId = 1L;
        Livro livroAtualizado = new Livro(livroId, "Livro Atualizado", "Autor Atualizado", "ISBN 111", 25.0, 4.0, 80, 30, 0);

        when(livroRepository.findById(livroId)).thenReturn(Optional.empty());

        ResponseEntity<LivroResponse> responseEntity = livroController.updateBook(livroId, livroAtualizado);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

        LivroResponse response = responseEntity.getBody();
        assertEquals("Livro não encontrado com o ID: " + livroId, response.getMessage());
    }

    @Test
    public void testDeleteBookWithNonExistingBook() {
        Long livroId = 1L;

        when(livroRepository.findById(livroId)).thenReturn(Optional.empty());
        when(livroRepository.existsById(livroId)).thenReturn(false);

        ResponseEntity<String> responseEntity = livroController.deleteBook(livroId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

        assertEquals("Livro não encontrado com o ID: " + livroId, responseEntity.getBody());
    }

    @Test
    public void testGetBooksByTitleWithMultipleResults() {
        // Simule uma lista de livros com o mesmo título
        String titulo = "Livro";
        List<Livro> livrosSimulados = new ArrayList<>();
        livrosSimulados.add(new Livro(1L, "Livro 1", "Autor 1", "ISBN 111", 20.0, 4.5, 100, 50, 0));
        livrosSimulados.add(new Livro(2L, "Livro 2", "Autor 2", "ISBN 222", 25.0, 4.0, 80, 30, 0));

        when(livroRepository.findByTittleContainingIgnoreCase(titulo)).thenReturn(livrosSimulados);

        ResponseEntity<List<Livro>> responseEntity = livroController.getBooksByTitle(titulo);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        assertFalse(responseEntity.getBody().isEmpty());

        assertEquals(livrosSimulados, responseEntity.getBody());
    }

    @Test
    public void testDeleteBookWithStock() {
        Long livroId = 1L;
        Livro livroComEstoque = new Livro();
        livroComEstoque.setId(livroId);
        livroComEstoque.setStock(10);

        when(livroRepository.findById(livroId)).thenReturn(Optional.of(livroComEstoque));

        ResponseEntity<String> responseEntity = livroController.deleteBook(livroId);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        assertEquals("Não é permitido excluir o livro, pois ainda há cópias em estoque.", responseEntity.getBody());
    }

    @Test
    public void testAddNegativeCopiesToStock() {
        Long livroId = 1L;
        Livro livroExistente = new Livro();
        livroExistente.setId(livroId);
        livroExistente.setStock(10);

        when(livroRepository.findById(livroId)).thenReturn(Optional.of(livroExistente));

        ResponseEntity<String> responseEntity = livroController.addCopiesToStock(livroId, -10);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        assertEquals("A quantidade a ser adicionada deve ser maior que zero.", responseEntity.getBody());
    }

    @Test
    public void testUpdateBookWithInvalidUserRating() {
        Long livroId = 1L;

        Livro livroExistente = new Livro();
        livroExistente.setId(livroId);
        livroExistente.setUserRating(4);

        Livro livroAtualizado = new Livro();
        livroAtualizado.setUserRating(6);

        when(livroRepository.findById(livroId)).thenReturn(Optional.of(livroExistente));

        ResponseEntity<LivroResponse> response = livroController.updateBook(livroId, livroAtualizado);

        assertEquals(400, response.getStatusCodeValue());

        assertTrue(response.getBody().getMessage().contains("Avaliação do usuário deve estar entre 1 e 5."));
    }

    @Test
    public void testGetBooksByTitle() {
        String titulo = "Livro 1";

        List<Livro> livrosSimulados = new ArrayList<>();
        livrosSimulados.add(new Livro(1L, "Livro 1", "Autor 1", "ISBN 111", 20.0, 4.5, 100, 50, 0));
        livrosSimulados.add(new Livro(2L, "Livro 1 Edição Especial", "Autor 2", "ISBN 222", 25.0, 4.0, 80, 30, 0));

        when(livroRepository.findByTittleContainingIgnoreCase(titulo)).thenReturn(livrosSimulados);

        ResponseEntity<List<Livro>> response = livroController.getBooksByTitle(titulo);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertFalse(response.getBody().isEmpty());

        assertEquals(livrosSimulados, response.getBody());
    }
}