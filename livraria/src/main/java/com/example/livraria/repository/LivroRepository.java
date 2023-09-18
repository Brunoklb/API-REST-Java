package com.example.livraria.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.livraria.model.Livro;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long>{

	List<Livro> findByTittleContainingIgnoreCase(String titulo);
}
