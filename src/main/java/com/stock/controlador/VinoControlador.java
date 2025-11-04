package com.stock.controlador;

import com.stock.entidades.Vino;
import com.stock.entidades.Bodega;
import com.stock.entidades.servicio.VinoService;
import com.stock.entidades.servicio.BodegaService;
import com.stock.entidades.servicio.VariedadService;
import com.stock.entidades.Variedad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

import com.stock.entidades.servicio.PrecioVinoService;
import java.math.BigDecimal;

@Controller
public class VinoControlador {

    @Autowired
    private VinoService vinoService;

    @Autowired
    private BodegaService bodegaService;

    @Autowired
    private VariedadService variedadService;

    @Autowired
    private PrecioVinoService precioVinoService;

    @GetMapping("/listarVinos")
    public String listarVinos(Model model) {
        List<Vino> vinos = vinoService.findAll();
        model.addAttribute("titulo", "Listado de Vinos");
        model.addAttribute("vinos", vinos);
        return "listarVinos";
    }

    @GetMapping("/vinoForm")
    public String crearVino(Map<String, Object> model) {
        Vino vino = new Vino();
        List<Bodega> bodegas = bodegaService.findAll();
        List<Variedad> variedades = variedadService.findAll();
        model.put("vino", vino);
        model.put("bodegas", bodegas);
        model.put("variedades", variedades);
        model.put("titulo", "Formulario de Vino");
        return "vinoForm";
    }

    @PostMapping("/vinoForm")
    public String guardarVino(@ModelAttribute("vino") Vino vino, BindingResult result, Model model, RedirectAttributes flash, SessionStatus status) {
        if (result.hasErrors()) {
            List<Bodega> bodegas = bodegaService.findAll();
            List<Variedad> variedades = variedadService.findAll();
            model.addAttribute("bodegas", bodegas);
            model.addAttribute("variedades", variedades);
            model.addAttribute("titulo", "Formulario de Vino");
            return "vinoForm";
        }
        vinoService.save(vino);
        status.setComplete();
        flash.addFlashAttribute("success", "Vino guardado con éxito");
        return "redirect:/listarVinos";
    }

    @GetMapping("/vinoForm/{id}")
    public String editarVino(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {
        Vino vino = null;
        if (id > 0) {
            vino = vinoService.findById(id);
            if (vino == null) {
                flash.addFlashAttribute("error", "El ID del vino no existe en la base de datos");
                return "redirect:/listarVinos";
            }
        } else {
            flash.addFlashAttribute("error", "El ID del vino no puede ser cero");
            return "redirect:/listarVinos";
        }
        List<Bodega> bodegas = bodegaService.findAll();
        List<Variedad> variedades = variedadService.findAll();
        model.put("vino", vino);
        model.put("bodegas", bodegas);
        model.put("variedades", variedades);
        model.put("titulo", "Editar Vino");
        return "vinoForm";
    }

    @GetMapping("/eliminarVino/{id}")
    public String eliminarVino(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
        if (id > 0) {
            vinoService.deleteById(id);
            flash.addFlashAttribute("success", "Vino eliminado con éxito");
        }
        return "redirect:/listarVinos";
    }

    @GetMapping(value = "/cargar-vinos", produces = {"application/json"})
    public @ResponseBody List<Vino> cargarVinos(@RequestParam("term") String term) {
        return vinoService.findByNombre(term);
    }

    @GetMapping("/obtener-precio-vino/{id}")
    @ResponseBody
    public BigDecimal obtenerPrecioVino(@PathVariable Long id) {
        Vino vino = vinoService.findById(id);
        if (vino != null) {
            BigDecimal precio = precioVinoService.findPrecioByVino(vino);
            if (precio != null) {
                return precio;
            }
        }
        return BigDecimal.ZERO;
    }
}