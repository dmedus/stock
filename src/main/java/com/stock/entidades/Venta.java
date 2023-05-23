package com.stock.entidades;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "venta")
public class Venta {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotEmpty
	private String cliente;
	
	@NotEmpty
	private String imei;
	
	@ManyToOne
	@JoinColumn(name = "modelo_id")
	private Modelo modelo;
	
	@NotNull
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date fecha;

	@NotEmpty
	private String moto;
	
	private int venta;
	
	private int costo;
	
	private int ganancia;
	
	private String tipoVenta;
	
	private String lugar;
	
	private String observacion;
	
	private boolean isCobrado;
	
	private String usuario;
	
	@OneToOne
	@JoinColumn(name = "stock_id")
	private Stock stock;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCliente() {
		return cliente;
	}

	public void setCliente(String cliente) {
		this.cliente = cliente;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getMoto() {
		return moto;
	}

	public void setMoto(String moto) {
		this.moto = moto;
	}

	public int getVenta() {
		return venta;
	}

	public void setVenta(int venta) {
		this.venta = venta;
	}

	public String getTipoVenta() {
		return tipoVenta;
	}

	public void setTipoVenta(String tipoVenta) {
		this.tipoVenta = tipoVenta;
	}

	public String getLugar() {
		return lugar;
	}

	public void setLugar(String lugar) {
		this.lugar = lugar;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public boolean isCobrado() {
		return isCobrado;
	}

	public void setCobrado(boolean isCobrado) {
		this.isCobrado = isCobrado;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public Modelo getModelo() {
		return modelo;
	}

	public void setModelo(Modelo modelo) {
		this.modelo = modelo;
	}

	public Stock getStock() {
		return stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}

	
	public int getGanancia() {
		return ganancia;
	}

	public void setGanancia(int ganancia) {
		this.ganancia = ganancia;
	}
	

	public int getCosto() {
		return costo;
	}

	public void setCosto(int costo) {
		this.costo = costo;
	}

	public Venta(Long id, @NotEmpty String cliente, @NotNull Date fecha, @NotEmpty String moto, int venta,
			String tipoVenta, String lugar, String observacion, boolean isCobrado, String usuario) {
		super();
		this.id = id;
		this.cliente = cliente;
		this.fecha = fecha;
		this.moto = moto;
		this.venta = venta;
		this.tipoVenta = tipoVenta;
		this.lugar = lugar;
		this.observacion = observacion;
		this.isCobrado = isCobrado;
		this.usuario = usuario;
	}

	public Venta() {
		super();
	}

	public Venta(@NotEmpty String cliente, @NotNull Date fecha, @NotEmpty String moto, int venta, String tipoVenta,
			String lugar, String observacion, boolean isCobrado, String usuario) {
		super();
		this.cliente = cliente;
		this.fecha = fecha;
		this.moto = moto;
		this.venta = venta;
		this.tipoVenta = tipoVenta;
		this.lugar = lugar;
		this.observacion = observacion;
		this.isCobrado = isCobrado;
		this.usuario = usuario;
	}

	
}
