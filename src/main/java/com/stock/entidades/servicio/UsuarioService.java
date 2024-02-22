 package com.stock.entidades.servicio;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.stock.controlador.dto.UsuarioDTO;
import com.stock.entidades.Usuario;

public interface UsuarioService extends UserDetailsService {

	public Usuario guardar(UsuarioDTO registroDTO);
	
	public List<Usuario> listarUsuarios(); 
	
	public Usuario findOne(Long id);
	
	public void delete(Long id); 
	
}
