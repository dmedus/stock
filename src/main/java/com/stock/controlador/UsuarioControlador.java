package com.stock.controlador;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.stock.controlador.dto.UsuarioDTO;
import com.stock.entidades.Usuario;
import com.stock.entidades.servicio.UsuarioService;

@Controller
public class UsuarioControlador {

	@Autowired
	UsuarioService usuarioService;
	
	@ModelAttribute("usuario")
	public UsuarioDTO retornarNuevoUsuarioRegistroDTO() {
		return new UsuarioDTO();
	}

	@GetMapping({"/listarUsuarios"})
	public String listarUsuarios(Model modelo) {		
		List<Usuario> listarUsuarios = usuarioService.listarUsuarios();		
		
		modelo.addAttribute("titulo","Listado de Usuario");
		modelo.addAttribute("listarUsuarios",listarUsuarios);

		return "listarUsuarios";
	}
	
	@GetMapping("/usuarioForm")
	public String mostrarFormularioDeUsuario(Map<String,Object> modelo) {
		Usuario usuario = new Usuario();
		
		modelo.put("usuario", usuario);		
		modelo.put("titulo", "Registro de Usuario");
		return "usuarioForm";
	}
	
	@PostMapping("/usuarioForm")
	public String guardarUsuario(@ModelAttribute("usuario") UsuarioDTO registroDTO,BindingResult result,Model modelo,RedirectAttributes flash,SessionStatus status,Authentication auth) {
		if(result.hasErrors()) {
			modelo.addAttribute("titulo", "Registro de Modelos");
			return "usuarioForm";
		}
		
		String mensaje = (registroDTO.getId() != null) ? "El Usuario ha sido editato con exito" : "Usuario registrado con exito";
		
		usuarioService.guardar(registroDTO);
		status.setComplete();
		flash.addFlashAttribute("success", mensaje);
		return "redirect:/listarUsuarios";
	}
	
	@GetMapping("/usuarioForm/{id}")
	public String editarUsuario(@PathVariable(value = "id") Long id,Map<String, Object> modelo,RedirectAttributes flash) {
		Usuario usuario = null;
		if(id > 0) {
			usuario = usuarioService.findOne(id);
			if(usuario == null) {
				flash.addFlashAttribute("error", "El ID del Usuario no existe en la base de datos");
				return "redirect:/listarUsuarios";
			}
		}
		else {
			flash.addFlashAttribute("error", "El ID del Usuario no puede ser cero");
			return "redirect:/listarUsuarios";
		}
		
		modelo.put("usuario",usuario);
		modelo.put("titulo", "Edición de stock");
		return "usuarioForm";
	}
	
	@GetMapping("/eliminarUsuario/{id}")
	public String eliminarUsuario(@PathVariable(value = "id") Long id,RedirectAttributes flash) {
		if(id > 0) {
			usuarioService.delete(id);
			flash.addFlashAttribute("success", "Usuario eliminado con exito");
		}
		return "redirect:/listarUsuarios";
	}
}
