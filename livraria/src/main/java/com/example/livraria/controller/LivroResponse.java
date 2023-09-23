package com.example.livraria.controller;

import com.example.livraria.model.Livro;

public class LivroResponse {
    private Livro livro;
    private String message;

    public LivroResponse(Livro livro, String message) {
        this.livro = livro;
        this.message = message;
    }

	public Livro getLivro() {
		return livro;
	}

	public void setLivro(Livro livro) {
		this.livro = livro;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
    
}

