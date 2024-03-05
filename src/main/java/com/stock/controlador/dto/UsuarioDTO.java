package com.stock.controlador.dto;

import com.stock.utils.reporte.Rol;

public class UsuarioDTO {

	private Long id;
	private String nombre;
	private String apellido;
	private String nomUsuario;
	private String email;
	private String password;
	private Rol rol;
	
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

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getUsuario() {
		return nomUsuario;
	}

	public void setUsuario(String usuario) {
		this.nomUsuario = usuario;
	}
	

	public Rol getRol() {
		return rol;
	}

	public void setRol(Rol rol) {
		this.rol = rol;
	}

	public UsuarioDTO() {

	}

	public UsuarioDTO(Long id, String nombre, String apellido, String usuario, String email, String password, Rol rol) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.apellido = apellido;
		this.nomUsuario = usuario;
		this.email = email;
		this.password = password;
		this.rol = rol;
	}

	
}
