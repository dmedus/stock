package com.stock.entidades;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "stock")
public class Stock {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	
	@ManyToOne
	@JoinColumn(name = "modelo_id")
	private Modelo modelo;
	
	@NotEmpty
	private String cond;
	
	@NotEmpty
	private String imei;	
	
	@NotEmpty
	private String proveedor;

	private int costo;
	
	private String tipoCosto;
	
	private String lugar;
	
	@NotNull
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fecha;
	
	private String observacion;
	
	private boolean inStock;
	
	private String usuario;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Modelo getModelo() {
		return modelo;
	}

	public void setModelo(Modelo modelo) {
		this.modelo = modelo;
	}

	public String getCond() {
		return cond;
	}

	public void setCond(String cond) {
		this.cond = cond;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getProveedor() {
		return proveedor;
	}

	public void setProveedor(String proveedor) {
		this.proveedor = proveedor;
	}

	public int getCosto() {
		return costo;
	}

	public void setCosto(int costo) {
		this.costo = costo;
	}

	public String getTipoCosto() {
		return tipoCosto;
	}

	public void setTipoCosto(String tipoCosto) {
		this.tipoCosto = tipoCosto;
	}

	public String getLugar() {
		return lugar;
	}

	public void setLugar(String lugar) {
		this.lugar = lugar;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public boolean isInStock() {
		return inStock;
	}

	public void setInStock(boolean inStock) {
		this.inStock = inStock;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public Stock(Long id, Modelo modelo, @NotEmpty String cond, @NotEmpty String imei, @NotEmpty String proveedor,
			int costo, String tipoCosto, String lugar, @NotNull Date fecha, String observacion, boolean inStock,
			String usuario) {
		super();
		this.id = id;
		this.modelo = modelo;
		this.cond = cond;
		this.imei = imei;
		this.proveedor = proveedor;
		this.costo = costo;
		this.tipoCosto = tipoCosto;
		this.lugar = lugar;
		this.fecha = fecha;
		this.observacion = observacion;
		this.inStock = inStock;
		this.usuario = usuario;
	}

	public Stock() {
		super();
	}

	public Stock(Modelo modelo, @NotEmpty String cond, @NotEmpty String imei, @NotEmpty String proveedor, int costo,
			String tipoCosto, String lugar, @NotNull Date fecha, String observacion, boolean inStock, String usuario) {
		super();
		this.modelo = modelo;
		this.cond = cond;
		this.imei = imei;
		this.proveedor = proveedor;
		this.costo = costo;
		this.tipoCosto = tipoCosto;
		this.lugar = lugar;
		this.fecha = fecha;
		this.observacion = observacion;
		this.inStock = inStock;
		this.usuario = usuario;
	}	
	



	
	
	
	

}
