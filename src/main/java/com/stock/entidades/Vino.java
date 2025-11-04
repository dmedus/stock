package com.stock.entidades;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "vinos")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Vino {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

    @Column(nullable = false, length = 200)
    private String nombre; // "El Enemigo Malbec"

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY)
    private Bodega bodega; // "El Enemigo"

    @ManyToOne(fetch = FetchType.LAZY)
    private Variedad variedad;

    private Integer anioCosecha; // 2022, 2018, null

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private Integer stockMinimo = 0;
    
    @Column(nullable = false)
    private Integer cantVinosxcaja = 6;

    // Stock siempre en BOTELLAS como unidad base

    private String imagenUrl;

    @Column(nullable = false)
    private Boolean activo = true;

    @javax.persistence.Transient
    private Integer stockActual;

    // Getters and Setters
    public Integer getCantVinosxcaja() {
        return cantVinosxcaja;
    }

    public void setCantVinosxcaja(Integer cantVinosxcaja) {
        this.cantVinosxcaja = cantVinosxcaja;
    }

    public String getNombre() {
        return nombre;
    }

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Bodega getBodega() {
        return bodega;
    }

    public void setBodega(Bodega bodega) {
        this.bodega = bodega;
    }

    public Variedad getVariedad() {
        return variedad;
    }

    public void setVariedad(Variedad variedad) {
        this.variedad = variedad;
    }

    public Integer getAnioCosecha() {
        return anioCosecha;
    }

    public void setAnioCosecha(Integer anioCosecha) {
        this.anioCosecha = anioCosecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }


    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Integer getStockActual() {
        return stockActual;
    }

    public void setStockActual(Integer stockActual) {
        this.stockActual = stockActual;
    }
}