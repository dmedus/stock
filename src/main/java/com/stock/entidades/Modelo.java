package com.stock.entidades;



import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "modelos")
public class Modelo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotEmpty
	private String nombre;	
	
	@NotEmpty
	private String capacidad;	
	
	@NotEmpty
	private String color;	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCapacidad() {
		return capacidad;
	}

	public void setCapacidad(String capacidad) {
		this.capacidad = capacidad;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Modelo(Long id, String nombre, String capacidad , String color) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.color = color;
		this.capacidad = capacidad;
	}

	public Modelo() {
		super();
	}

	public Modelo(String nombre, String capacidad , String color) {
		super();
		this.nombre = nombre;
		this.color = color;
		this.capacidad = capacidad;
	}
	
	
}
