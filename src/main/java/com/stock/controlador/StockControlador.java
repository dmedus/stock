package com.stock.controlador;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lowagie.text.DocumentException;
import com.stock.entidades.Modelo;
import com.stock.entidades.Stock;
import com.stock.entidades.servicio.StockService;
import com.stock.repositorio.ModeloRepository;
import com.stock.utils.paginacion.PageRender;
import com.stock.utils.reporte.StockExporterExcel;
import com.stock.utils.reporte.StockExporterPDF;



@Controller
public class StockControlador {

	@Autowired
	private StockService service;
	
	@Autowired
	private ModeloRepository modeloRepository;
	
	@GetMapping("/ver/{id}")
	public String verDetallesDelStock(@PathVariable(value = "id") Long id,Map<String,Object> modelo,RedirectAttributes flash) {
		Stock stock = service.findOne(id);
		if (stock == null) {
			flash.addAttribute("error", "El stock no existe en la base de dato");
			return "redirect:/listarStocks";
		}
		
		modelo.put("stock", stock);
		modelo.put("titulo", "Detalles del Stock" + stock.getId());
		return "ver";
	}
	
	
	@GetMapping({"/","/listarStocks",""})
	public String ListarStocks(@RequestParam(name = "page", defaultValue = "0") int page, Model modelo,@Param("palabraClave") String palabraClave,@Param("enStock") String enStock) {
		if (palabraClave ==  null) {
			palabraClave = "";
		}
		
		boolean inStock = true;
		if (enStock !=  null) {
			 inStock = false;
		}
				
		Pageable pageRequest = PageRequest.of(page, 5);
		Page<Stock> stocks = service.findAllPage(palabraClave,inStock,pageRequest);
		PageRender<Stock> pageRender = new PageRender<>("/listarStocks", stocks);
		
		modelo.addAttribute("titulo","Listado de Stock");
		modelo.addAttribute("stocks",stocks);
		modelo.addAttribute("page",pageRender);
		return "listarStocks";
	}
	
	
	@GetMapping("/form")
	public String mostrarFormularioDeStock(Map<String,Object> modelo) {
		Stock stock = new Stock();
		List<Modelo> listModelo = modeloRepository.findAll();
		
		modelo.put("stock", stock);
		modelo.put("listModelo", listModelo);
		modelo.put("titulo", "Registro de stock");
		return "form";
	}
	
	@PostMapping("/form")
	public String guardarStock(@Valid Stock stock,BindingResult result,Model modelo,RedirectAttributes flash,SessionStatus status,Authentication auth) {
		if(result.hasErrors()) {
			modelo.addAttribute("titulo", "Registro de stock");
			return "form";
		}
		
		String mensaje = (stock.getId() != null) ? "El stock ha sido editato con exito" : "stock registrado con exito";
		

		stock.setInStock(true);	
		stock.setUsuario(auth.getName());
		service.save(stock);
		status.setComplete();
		flash.addFlashAttribute("success", mensaje);
		return "redirect:/listarStocks";
	}
	
	@GetMapping("/form/{id}")
	public String editarStock(@PathVariable(value = "id") Long id,Map<String, Object> modelo,RedirectAttributes flash) {
		Stock stock = null;
		if(id > 0) {
			stock = service.findOne(id);
			if(stock == null) {
				flash.addFlashAttribute("error", "El ID del stock no existe en la base de datos");
				return "redirect:/listarStocks";
			}
		}
		else {
			flash.addFlashAttribute("error", "El ID del stock no puede ser cero");
			return "redirect:/listarStocks";
		}
		
		List<Modelo> listModelo = modeloRepository.findAll();
		
		modelo.put("listModelo", listModelo);
		modelo.put("stock",stock);
		modelo.put("titulo", "Edición de stock");
		return "form";
	}

	
	@GetMapping("/eliminar/{id}")
	public String eliminarStock(@PathVariable(value = "id") Long id,RedirectAttributes flash) {
		if(id > 0) {
			service.delete(id);
			flash.addFlashAttribute("success", "Stock eliminado con exito");
		}
		return "redirect:/listarStocks";
	}
	
	@GetMapping("/exportarPDF")
	public void exportarListadoDeEmpleadosEnPDF(HttpServletResponse response) throws DocumentException, IOException {
		response.setContentType("application/pdf");
		
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String fechaActual = dateFormatter.format(new Date());
		
		String cabecera = "Content-Disposition";
		String valor = "attachment; filename=Empleados_" + fechaActual + ".pdf";
		
		response.setHeader(cabecera, valor);
		
		List<Stock> empleados = service.findAll(null);
		
		StockExporterPDF exporter = new StockExporterPDF(empleados);
		exporter.exportar(response);
		
	}
	
	@GetMapping("/exportarExcel")
	public void exportarListadoDeEmpleadosEnExcel(HttpServletResponse response) throws DocumentException, IOException {
		response.setContentType("application/octet-stream");
		
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String fechaActual = dateFormatter.format(new Date());
		
		String cabecera = "Content-Disposition";
		String valor = "attachment; filename=Empleados_" + fechaActual + ".xlsx";
		
		response.setHeader(cabecera, valor);
		
		List<Stock> stock = service.findAll(null);
		
		StockExporterExcel exporter = new 	StockExporterExcel(stock);
		exporter.exportar(response);
	}
	

}
