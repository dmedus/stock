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
import com.stock.entidades.Stock;
import com.stock.entidades.Venta;
import com.stock.entidades.servicio.StockService;
import com.stock.entidades.servicio.VentaService;
import com.stock.repositorio.ModeloRepository;



@Controller
public class VentaControlador {

	@Autowired
	private VentaService service;
	
	@Autowired
	private StockService stockService;
	
	@Autowired
	private ModeloRepository modeloRepository;
	
	@GetMapping({"/listarVentas"})
	public String listarVentas(Model modelo) {		
		List<Venta> listarVentas = service.findAll();		
		
		modelo.addAttribute("titulo","Listado de Ventas");
		modelo.addAttribute("listarVentas",listarVentas);

		return "listarVentas";
	}

	@GetMapping("/ventaForm")
	public String mostrarFormularioDeVenta(Map<String,Object> modelo) {
		Venta venta = new Venta();		
		
		List<Modelo> listModelo = modeloRepository.findAll();
		modelo.put("venta", venta);	

		modelo.put("listModelo", listModelo);
		modelo.put("titulo", "Registro de Venta");
		return "ventaForm";
	}
		
	@PostMapping("/ventaForm/{id}")
	public String guardarVentas(@PathVariable(value = "id") Long id,@Valid Venta venta,BindingResult result,Model modelo,RedirectAttributes flash,SessionStatus status,Authentication auth) {
		Stock stock = null;
		if(id > 0) {
			stock = stockService.findOne(id);
			if(stock == null) {
				flash.addFlashAttribute("error", "El ID del stock no existe en la base de datos");
				return "redirect:/listarVentas";
			}
		}
		else {
			flash.addFlashAttribute("error", "El ID del modelo no puede ser cero");
			return "redirect:/listarVentas";
		}
		
		venta.setStock(stock);
		venta.setImei(stock.getImei());
		venta.setModelo(stock.getModelo());
		venta.setCosto(stock.getCosto());
		venta.setGanancia(venta.getVenta() - stock.getCosto());
		venta.setUsuario(auth.getName());

		String mensaje = (venta.getId() != null) ? "La venta ha sido editato con exito" : "La venta registrada con exito";
		
		service.save(venta);
		
		stock.setInStock(false);
		stockService.save(stock);
		status.setComplete();
		flash.addFlashAttribute("success", mensaje);
		return "redirect:/listarVentas";
	}
	

	@GetMapping("/ventaForm/{id}")
	public String mostrarFormularioDeVentas(@PathVariable(value = "id") Long id,Map<String, Object> modelo,RedirectAttributes flash) {
		Stock stock = null;
		if(id > 0) {
			stock = stockService.findOne(id);
			if(stock == null) {
				flash.addFlashAttribute("error", "El ID del stock no existe en la base de datos");
				return "redirect:/listarVentas";
			}
		}
		else {
			flash.addFlashAttribute("error", "El ID del modelo no puede ser cero");
			return "redirect:/listarVentas";
		}
		
		Venta venta = new Venta();
	
		modelo.put("stock", stock);
		modelo.put("venta", venta);		
		modelo.put("titulo", "Registro de Venta");
		return "ventaForm";
	}
	
		
	@GetMapping("/eliminarVenta/{id}")
	public String eliminarVenta(@PathVariable(value = "id") Long id,RedirectAttributes flash) {
		if(id > 0) {
			service.delete(id);
			flash.addFlashAttribute("success", "Venta se eliminado con exito");
		}
		return "redirect:/listarVentas";
	}
	
	
}
