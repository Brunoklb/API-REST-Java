package com.example.livraria.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.livraria.model.Livro;
import com.example.livraria.repository.LivroRepository;

@RestController
@RequestMapping("/livro")
public class livroController {

	@Autowired
	private LivroRepository livroRepository;

	// Retorna todos os livros
	@GetMapping
	public List<Livro> getAllBooks() {
		return livroRepository.findAll();
	}

	// Criar livro
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Livro createBook(@RequestBody Livro livro) {
		return livroRepository.save(livro);
	}

	// Deletar livro por ID
	@DeleteMapping("/{id}")
	public String deleteBook(@PathVariable Long id) {
		Optional<Livro> livroToDeleteOptional = livroRepository.findById(id);

		if (livroToDeleteOptional.isPresent()) {
			Livro livroToDelete = livroToDeleteOptional.get();

			if (livroToDelete.getStock() > 0) {
				return "Não é permitido excluir o livro, pois ainda há cópias em estoque.";
			} else {
				livroRepository.deleteById(id);
				return "Livro excluído com sucesso.";
			}
		} else {
			return "Livro não encontrado.";
		}
	}

	// Atualizar livro por ID
	@PutMapping("/{id}")
	public Livro updateBook(@PathVariable Long id, @RequestBody Livro livroAtualizado) {
		Optional<Livro> livroExistenteOptional = livroRepository.findById(id);

		if (livroExistenteOptional.isPresent()) {
			Livro livroExistente = livroExistenteOptional.get();

			livroExistente.setTittle(livroAtualizado.getTittle());
			livroExistente.setAuthor(livroAtualizado.getAuthor());
			livroExistente.setIsbn(livroAtualizado.getIsbn());
			livroExistente.setPrice(livroAtualizado.getPrice());
			livroExistente.setStock(livroAtualizado.getStock());

			int newUserRating = livroAtualizado.getUserRating();
			if (newUserRating >= 1 && newUserRating <= 5) {
				livroExistente.setUserRating(newUserRating);

				double currentAverageRating = livroExistente.getAverageRating();
				int numberOfRatings = livroExistente.getNumberOfRatings();

				double newAverageRating = ((currentAverageRating * numberOfRatings) + newUserRating)
						/ (numberOfRatings + 1);
				livroExistente.setAverageRating(newAverageRating);
				livroExistente.setNumberOfRatings(numberOfRatings + 1);
			} else {
				throw new IllegalArgumentException("Avaliação do usuário deve estar entre 1 e 5.");
			}

			Livro livroAtualizadoNoBanco = livroRepository.save(livroExistente);

			return livroAtualizadoNoBanco;
		} else {
			throw new LivroNotFoundException("Livro não encontrado com o ID: " + id);
		}
	}

	// Busca livro por título
	@GetMapping("/titulo/{titulo}")
	public List<Livro> getBooksByTitle(@PathVariable String titulo) {
		List<Livro> livros = livroRepository.findByTittleContainingIgnoreCase(titulo);
		return livros;
	}

	// Adicionar cópias ao estoque de um livro good
	@PostMapping("/{id}/add-copies/{quantity}")
	public String addCopiesToStock(@PathVariable Long id, @PathVariable Integer quantity) {
		Optional<Livro> livroOptional = livroRepository.findById(id);

		if (livroOptional.isPresent()) {
			Livro livro = livroOptional.get();
			int currentStock = livro.getStock();

			if (quantity > 0) {
				livro.setStock(currentStock + quantity);
				livroRepository.save(livro);
				return "Adicionadas " + quantity + " cópias ao estoque do livro.";
			} else {
				return "A quantidade a ser adicionada deve ser maior que zero.";
			}
		} else {
			return "Livro não encontrado.";
		}
	}

	// Remover cópias do estoque de um livro
	@PostMapping("/{id}/remove-copies/{quantity}")
	public String removeCopiesFromStock(@PathVariable Long id, @PathVariable Integer quantity) {
		Optional<Livro> livroOptional = livroRepository.findById(id);

		if (livroOptional.isPresent()) {
			Livro livro = livroOptional.get();
			int currentStock = livro.getStock();

			if (quantity > 0 && quantity <= currentStock) {
				livro.setStock(currentStock - quantity);
				livroRepository.save(livro);
				return "Removidas " + quantity + " cópias do estoque do livro.";
			} else if (quantity <= 0) {
				return "A quantidade a ser removida deve ser maior que zero.";
			} else {
				return "A quantidade a ser removida excede o estoque atual.";
			}
		} else {
			return "Livro não encontrado.";
		}
	}

}
