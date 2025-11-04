package com.stock.entidades.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stock.controlador.dto.UsuarioDTO;
import com.stock.entidades.Usuario;
import com.stock.repositorio.UsuarioRepository;

@Service 
public class UsuarioServiceImpl implements UsuarioService{

	@Autowired
	private UsuarioRepository repository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Override
	public Usuario guardar(Usuario registroDTO) {
		Usuario usuario = new Usuario(registroDTO.getId(),registroDTO.getNombre(), 
				registroDTO.getApellido(),registroDTO.getUsuario(),registroDTO.getEmail(),
				passwordEncoder.encode(registroDTO.getPassword()),registroDTO.getRol() );
		return repository.save(usuario);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = repository.findByUsuario(username);
		if (usuario == null) {
			throw new UsernameNotFoundException("Usuario o password inválidos");
		}
		String role = usuario.getRol().name();

		return User.builder().username(usuario.getUsuario()).password(usuario.getPassword()).roles(role).build();
	}
	
	
	@Override
	public List<Usuario> listarUsuarios() {
		return repository.findAll();
	}

	@Override
	@Transactional
	public Usuario findOne(Long id) {
		return repository.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		repository.deleteById(id);
		
	}



}
