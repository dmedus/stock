package com.stock.controlador;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.stock.entidades.Clientes;
import com.stock.entidades.servicio.ClientesService;
import com.stock.utils.paginacion.PageRender;



@Controller
public class ClientesControlador {

	@Autowired
	ClientesService clientesServices;
	


	@GetMapping({"/listarClientes"})
	public String listarClientes(@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "palabraClave", required = false) String palabraClave,
			Model modelo) {
		Pageable pageable = PageRequest.of(page, 10);
		Page<Clientes> pageClientes = clientesServices.findAll(pageable, palabraClave);
		PageRender<Clientes> pageRender = new PageRender<>("/listarClientes", pageClientes);

		modelo.addAttribute("titulo","Listado de clientes");
		modelo.addAttribute("listarClientes",pageClientes.getContent());
		modelo.addAttribute("palabraClave", palabraClave);
		modelo.addAttribute("page", pageRender);

		return "listarClientes";
	}
	
	@GetMapping("/clientesForm")
	public String mostrarFormularioDeClientes(Map<String,Object> modelo) {
		Clientes cliente = new Clientes();
		
		modelo.put("cliente", cliente);
		modelo.put("editar",true);
		modelo.put("titulo", "Registro de Clientes");
		return "clientesForm";
	}
	
	@PostMapping("/clientesForm")
	public String guardarClientes(@ModelAttribute("cliente") Clientes clientes,BindingResult result,Model modelo,RedirectAttributes flash,SessionStatus status,Authentication auth) {
		if(result.hasErrors()) {
			modelo.addAttribute("titulo", "Registro de Clientes");
			return "clientesForm";
		}
		
		String mensaje = (clientes.getId() != null) ? "El Clientes ha sido editato con exito" : "Clientes registrado con exito";
		
		clientesServices.guardar(clientes);
		status.setComplete();
		flash.addFlashAttribute("success", mensaje);
		return "redirect:/listarClientes";
	}
	
	@GetMapping("/clientesForm/{id}")
	public String editarClientes(@PathVariable(value = "id") Long id,Map<String, Object> modelo,RedirectAttributes flash) {
		Clientes clientes = null;
		if(id > 0) {
			clientes = clientesServices.findById(id);
			if(clientes == null) {
				flash.addFlashAttribute("error", "El ID del Clientes no existe en la base de datos");
				return "redirect:/listarClientes";
			}
		}
		else {
			flash.addFlashAttribute("error", "El ID del Clientes no puede ser cero");
			return "redirect:/listarClientes";
		}
		
		modelo.put("cliente",clientes);
		modelo.put("editar",false);
		modelo.put("titulo", "Edición de Clientes");
		return "clientesForm";
	}
	
	@GetMapping("/eliminarClientes/{id}")
	public String eliminarClientes(@PathVariable(value = "id") Long id,RedirectAttributes flash) {
		if(id > 0) {
			clientesServices.delete(id);
			flash.addFlashAttribute("success", "Cliente eliminado con exito");
		}
		return "redirect:/listarClientes";
	}

    @GetMapping(value = "/cargar-clientes", produces = {"application/json"})
    public @ResponseBody List<Clientes> cargarClientes(@RequestParam("term") String term) {
        return clientesServices.findByNombreOrApellido(term);
    }
}
