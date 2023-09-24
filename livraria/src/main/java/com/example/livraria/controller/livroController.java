package com.example.livraria.controller;

import com.example.livraria.model.Livro;
import com.example.livraria.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/livro")
public class livroController {

	@Autowired
	private LivroRepository livroRepository;

	// Retorna todos os livros
	@GetMapping
	public ResponseEntity<List<Livro>> getAllBooks() {
		List<Livro> livros = livroRepository.findAll();

		if (livros.isEmpty()) {
			return ((BodyBuilder) ResponseEntity.notFound()).body(null);
		}
		return ResponseEntity.ok(livros);
	}

	// Create book
	@PostMapping
	public ResponseEntity<LivroResponse> createBook(@RequestBody Livro livro) {
		if (livroRepository.existsByIsbn(livro.getIsbn())) {
			String mensagem = "Já existe um livro com o ISBN: " + livro.getIsbn();
			LivroResponse response = new LivroResponse(livro, mensagem);
			return ResponseEntity.badRequest().body(response);
		}

		if (livro.getStock() < 0) {
			LivroResponse response = new LivroResponse(livro, "O estoque não pode ser negativo");
			return ResponseEntity.badRequest().body(response);
		}

		Livro livroSalvo = livroRepository.save(livro);
		LivroResponse response = new LivroResponse(livroSalvo, "Livro criado com sucesso");
		return ResponseEntity.created(null).body(response);
	}

	// Delete book by id
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteBook(@PathVariable Long id) {
		Optional<Livro> livroToDeleteOptional = livroRepository.findById(id);

		if (livroToDeleteOptional.isPresent()) {
			Livro livroToDelete = livroToDeleteOptional.get();

			if (livroToDelete.getStock() != null && livroToDelete.getStock() > 0) {
				return ResponseEntity.badRequest()
						.body("Não é permitido excluir o livro, pois ainda há cópias em estoque.");
			} else {
				livroRepository.deleteById(id);
				return ResponseEntity.ok("Livro excluído com sucesso.");
			}
		} else {
			return ((BodyBuilder) ResponseEntity.notFound()).body("Livro não encontrado com o ID: " + id);
		}
	}


	// Atualizar livro por ID
	@PutMapping("/{id}")
	public ResponseEntity<LivroResponse> updateBook(@PathVariable Long id, @RequestBody Livro livroAtualizado) {
		Optional<Livro> livroExistenteOptional = livroRepository.findById(id);

		if (livroExistenteOptional.isPresent()) {
			Livro livroExistente = livroExistenteOptional.get();

			int newStock = livroAtualizado.getStock();
			if (newStock >= 0) {
				livroExistente.setTittle(livroAtualizado.getTittle());
				livroExistente.setAuthor(livroAtualizado.getAuthor());
				livroExistente.setIsbn(livroAtualizado.getIsbn());
				livroExistente.setPrice(livroAtualizado.getPrice());
				livroExistente.setStock(newStock);

				int newUserRating = livroAtualizado.getUserRating();
				if (newUserRating >= 1 && newUserRating <= 5) {
					livroExistente.setUserRating(newUserRating);

					double currentAverageRating = livroExistente.getAverageRating();
					int numberOfRatings = livroExistente.getNumberOfRatings();

					double newAverageRating = ((currentAverageRating * numberOfRatings) + newUserRating)
							/ (numberOfRatings + 1);
					livroExistente.setAverageRating(newAverageRating);
					livroExistente.setNumberOfRatings(numberOfRatings + 1);

					Livro livroAtualizadoNoBanco = livroRepository.save(livroExistente);

					LivroResponse response = new LivroResponse(livroAtualizadoNoBanco, "Livro atualizado com sucesso");
					return ResponseEntity.ok(response);
				} else {
					LivroResponse response = new LivroResponse(null, "Avaliação do usuário deve estar entre 1 e 5.");
					return ResponseEntity.badRequest().body(response);
				}
			} else {
				LivroResponse response = new LivroResponse(null, "O valor de estoque não pode ser negativo.");
				return ResponseEntity.badRequest().body(response);
			}
		} else {
			LivroResponse response = new LivroResponse(null, "Livro não encontrado com o ID: " + id);
			return ((BodyBuilder) ResponseEntity.notFound()).body(response);
		}
	}

	// Busca livro por título
	@GetMapping("/titulo/{titulo}")
	public ResponseEntity<List<Livro>> getBooksByTitle(@PathVariable String titulo) {
		List<Livro> livros = livroRepository.findByTittleContainingIgnoreCase(titulo);

		if (livros.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(livros);
	}

	// Adicionar cópias ao estoque de um livro good
	@PostMapping("/{id}/add-copies/{quantity}")
	public ResponseEntity<String> addCopiesToStock(@PathVariable Long id, @PathVariable Integer quantity) {
		Optional<Livro> livroOptional = livroRepository.findById(id);

		if (livroOptional.isPresent()) {
			Livro livro = livroOptional.get();
			int currentStock = livro.getStock();

			if (quantity > 0) {
				livro.setStock(currentStock + quantity);
				livroRepository.save(livro);
				String message = "Adicionadas " + quantity + " cópias ao estoque do livro.";
				return ResponseEntity.ok(message);
			} else {
				return ResponseEntity.badRequest().body("A quantidade a ser adicionada deve ser maior que zero.");
			}
		} else {
			return ((BodyBuilder) ResponseEntity.notFound()).body("Livro não encontrado.");
		}
	}

	// Remover cópias do estoque de um livro
	@PostMapping("/{id}/remove-copies/{quantity}")
	public ResponseEntity<String> removeCopiesFromStock(@PathVariable Long id, @PathVariable Integer quantity) {
		Optional<Livro> livroOptional = livroRepository.findById(id);

		if (livroOptional.isPresent()) {
			Livro livro = livroOptional.get();
			int currentStock = livro.getStock();

			if (quantity > 0 && quantity <= currentStock) {
				livro.setStock(currentStock - quantity);
				livroRepository.save(livro);
				String message = "Removidas " + quantity + " cópias do estoque do livro.";
				return ResponseEntity.ok(message);
			} else if (quantity <= 0) {
				return ResponseEntity.badRequest().body("A quantidade a ser removida deve ser maior que zero.");
			} else {
				return ResponseEntity.badRequest().body("A quantidade a ser removida excede o estoque atual.");
			}
		} else {
			return ((BodyBuilder) ResponseEntity.notFound()).body("Livro não encontrado.");
		}
	}
}
