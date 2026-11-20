package com.shopwise.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "DISCOUNT")
public class Discount {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Réduction (FLOAT dans le schéma)
	@Column(nullable = false)
	private Float reduce;

	public Discount() {
	}

	public Discount(Float reduce) {
		this.reduce = reduce;
	}

	public Long getId() {
		return id;
	}

	public Float getReduce() {
		return reduce;
	}

	public void setReduce(Float reduce) {
		this.reduce = reduce;
	}

}
