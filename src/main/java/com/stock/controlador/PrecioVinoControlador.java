package com.stock.controlador;

import com.stock.entidades.PrecioVino;
import com.stock.entidades.Vino;
import com.stock.entidades.ListaPrecio;
import com.stock.entidades.servicio.PrecioVinoService;
import com.stock.entidades.servicio.VinoService;
import com.stock.entidades.servicio.ListaPrecioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import com.stock.utils.reporte.PrecioVinoExporterExcel;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class PrecioVinoControlador {

    @Autowired
    private PrecioVinoService precioVinoService;

    @Autowired
    private VinoService vinoService;

    @Autowired
    private ListaPrecioService listaPrecioService;

    @GetMapping("/listarPreciosVino")
    public String listarPreciosVino(@RequestParam(required = false) Long listaId, Model model) {
        List<PrecioVino> preciosVino;
        List<ListaPrecio> listasPrecio = listaPrecioService.findAll();

        if (listaId != null) {
            ListaPrecio listaPrecio = listaPrecioService.findById(listaId);
            preciosVino = precioVinoService.findByListaPrecio(listaPrecio);
        } else {
            preciosVino = precioVinoService.findAll();
        }

        model.addAttribute("titulo", "Listado de Precios de Vino");
        model.addAttribute("preciosVino", preciosVino);
        model.addAttribute("listasPrecio", listasPrecio);
        model.addAttribute("listaId", listaId);
        return "listarPreciosVino";
    }

    @GetMapping("/precioVinoForm")
    public String crearPrecioVino(Map<String, Object> model) {
        PrecioVino precioVino = new PrecioVino();
        List<Vino> vinos = vinoService.findAll();
        List<ListaPrecio> listasPrecio = listaPrecioService.findAll();
        model.put("precioVino", precioVino);
        model.put("vinos", vinos);
        model.put("listasPrecio", listasPrecio);
        model.put("titulo", "Formulario de Precio de Vino");
        return "precioVinoForm";
    }

    @PostMapping("/precioVinoForm")
    public String guardarPrecioVino(@ModelAttribute("precioVino") PrecioVino precioVino, BindingResult result, Model model, RedirectAttributes flash, SessionStatus status) {
        if (result.hasErrors()) {
            List<Vino> vinos = vinoService.findAll();
            List<ListaPrecio> listasPrecio = listaPrecioService.findAll();
            model.addAttribute("vinos", vinos);
            model.addAttribute("listasPrecio", listasPrecio);
            model.addAttribute("titulo", "Formulario de Precio de Vino");
            return "precioVinoForm";
        }
        precioVinoService.save(precioVino);
        status.setComplete();
        flash.addFlashAttribute("success", "Precio de Vino guardado con éxito");
        return "redirect:/listarPreciosVino";
    }

    @GetMapping("/precioVinoForm/{id}")
    public String editarPrecioVino(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {
        PrecioVino precioVino = null;
        if (id > 0) {
            precioVino = precioVinoService.findById(id);
            if (precioVino == null) {
                flash.addFlashAttribute("error", "El ID del precio de vino no existe en la base de datos");
                return "redirect:/listarPreciosVino";
            }
        } else {
            flash.addFlashAttribute("error", "El ID del precio de vino no puede ser cero");
            return "redirect:/listarPreciosVino";
        }
        List<Vino> vinos = vinoService.findAll();
        List<ListaPrecio> listasPrecio = listaPrecioService.findAll();
        model.put("precioVino", precioVino);
        model.put("vinos", vinos);
        model.put("listasPrecio", listasPrecio);
        model.put("titulo", "Editar Precio de Vino");
        return "precioVinoForm";
    }

    @GetMapping("/eliminarPrecioVino/{id}")
    public String eliminarPrecioVino(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
        if (id > 0) {
            precioVinoService.deleteById(id);
            flash.addFlashAttribute("success", "Precio de Vino eliminado con éxito");
        }
        return "redirect:/listarPreciosVino";
    }

    @GetMapping("/exportarPreciosVino")
    public void exportarPreciosVino(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=precios_vino_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<PrecioVino> listaPreciosVino = precioVinoService.findAll();

        PrecioVinoExporterExcel exporter = new PrecioVinoExporterExcel(listaPreciosVino);
        exporter.export(response);
    }

    @PostMapping("/importarPreciosVino")
    public String importarPreciosVino(@RequestParam("file") MultipartFile file, RedirectAttributes flash) {
        if (file.isEmpty()) {
            flash.addFlashAttribute("error", "Por favor, seleccione un archivo para subir.");
            return "redirect:/listarPreciosVino";
        }

        try {
            precioVinoService.importarPreciosDesdeExcel(file.getInputStream());
            flash.addFlashAttribute("success", "Los precios se han importado correctamente.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al importar el archivo: " + e.getMessage());
        }

        return "redirect:/listarPreciosVino";
    }
}
