package com.stock.controlador;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.stock.entidades.Modelo;
import com.stock.entidades.servicio.ModeloService;



@Controller
public class ModeloControlador {

	@Autowired
	private ModeloService service;
	
		
	@GetMapping({"/listarModelos"})
	public String listarModelos(Model modelo) {		
		List<Modelo> listModelos = service.findAll();		
		
		modelo.addAttribute("titulo","Listado de Modelos");
		modelo.addAttribute("listModelos",listModelos);

		return "listarModelos";
	}

	
		
	@GetMapping("/modeloForm")
	public String mostrarFormularioDeModelos(Map<String,Object> modelo) {
		Modelo modeloCel = new Modelo();
		
		modelo.put("modeloCel", modeloCel);		
		modelo.put("titulo", "Registro de Modelos");
		return "modeloForm";
	}
	
	@PostMapping("/modeloForm")
	public String guardarModelos(@Valid Modelo modeloCel,BindingResult result,Model modelo,RedirectAttributes flash,SessionStatus status,Authentication auth) {
		if(result.hasErrors()) {
			modelo.addAttribute("titulo", "Registro de Modelos");
			return "modeloForm";
		}
		
		String mensaje = (modeloCel.getId() != null) ? "El modelo ha sido editato con exito" : "modelo registrado con exito";
		
		service.save(modeloCel);
		status.setComplete();
		flash.addFlashAttribute("success", mensaje);
		return "redirect:/listarModelos";
	}
	
	@GetMapping("/modeloForm/{id}")
	public String editarModelos(@PathVariable(value = "id") Long id,Map<String, Object> modelo,RedirectAttributes flash) {
		Modelo modeloCel = null;
		if(id > 0) {
			modeloCel = service.findOne(id);
			if(modeloCel == null) {
				flash.addFlashAttribute("error", "El ID del modelo no existe en la base de datos");
				return "redirect:/listarModelos";
			}
		}
		else {
			flash.addFlashAttribute("error", "El ID del modelo no puede ser cero");
			return "redirect:/listarModelos";
		}
		
		modelo.put("modeloCel",modeloCel);
		modelo.put("titulo", "Edición de stock");
		return "modeloForm";
	}
	
	@GetMapping("/eliminarModelo/{id}")
	public String eliminarModelos(@PathVariable(value = "id") Long id,RedirectAttributes flash) {
		if(id > 0) {
			service.delete(id);
			flash.addFlashAttribute("success", "Modelo eliminado con exito");
		}
		return "redirect:/listarModelos";
	}
	
	
}
