package com.example.livraria.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Livro {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String tittle;
	private String author;
	private String isbn;
	private Double price;
	private Double averageRating;
	private Integer numberOfRatings;
	private Integer stock;
	private int userRating;

	public Livro(long l, String s, String s1, String s2, double v, double v1, int i, int i1, int i2) {
	}

	public Livro() {
		this.stock = 0;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTittle() {
		return tittle;
	}

	public void setTittle(String tittle) {
		this.tittle = tittle;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(Double averageRating) {
		this.averageRating = averageRating;
	}

	public Integer getNumberOfRatings() {
		return numberOfRatings;
	}

	public void setNumberOfRatings(Integer numberOfRatings) {
		this.numberOfRatings = numberOfRatings;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public int getUserRating() {
		return userRating;
	}

	public void setUserRating(int userRating) {
		this.userRating = userRating;
	}

	@Override
	public int hashCode() {
		return Objects.hash(author, averageRating, id, isbn, numberOfRatings, price, stock, tittle);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Livro other = (Livro) obj;
		return Objects.equals(author, other.author) && Objects.equals(averageRating, other.averageRating)
				&& Objects.equals(id, other.id) && Objects.equals(isbn, other.isbn)
				&& Objects.equals(numberOfRatings, other.numberOfRatings) && Objects.equals(price, other.price)
				&& Objects.equals(stock, other.stock) && Objects.equals(tittle, other.tittle);
	}

	@Override
	public String toString() {
		return "Livro [id=" + id + ", tittle=" + tittle + ", author=" + author + ", isbn=" + isbn + ", price=" + price
				+ ", averageRating=" + averageRating + ", numberOfRatings=" + numberOfRatings + ", stock=" + stock
				+ "]";
	}

}