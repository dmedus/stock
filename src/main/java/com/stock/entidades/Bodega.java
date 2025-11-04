package com.stock.entidades;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Index;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "bodegas", indexes = {
    @Index(name = "idx_bodega_nombre", columnList = "nombre")
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Bodega {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

    @Column(nullable = false, unique = true, length = 200)
    private String nombre; // "El Enemigo", "Catena Zapata"

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(length = 200)
    private String region; // "Mendoza", "Salta", etc.

    @Column(length = 200)
    private String subregion; // "Gualtallalry", "Agrelo", "Valle de Uco"

    @Column(nullable = false)
    private Boolean activa = true;

    @JsonBackReference
    @OneToMany(mappedBy = "bodega", fetch = FetchType.LAZY)
    private List<Vino> vinos = new ArrayList<>();
    

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	// Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getSubregion() {
        return subregion;
    }

    public void setSubregion(String subregion) {
        this.subregion = subregion;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }

    public List<Vino> getVinos() {
        return vinos;
    }

    public void setVinos(List<Vino> vinos) {
        this.vinos = vinos;
    }

    @Transient
    public int getCantidadVinos() {
        return vinos.size();
    }
}
