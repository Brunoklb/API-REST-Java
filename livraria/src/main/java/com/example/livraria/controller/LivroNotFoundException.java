package com.example.livraria.controller;

public class LivroNotFoundException extends RuntimeException {

	public LivroNotFoundException(String message) {
		super(message);
	}
}
