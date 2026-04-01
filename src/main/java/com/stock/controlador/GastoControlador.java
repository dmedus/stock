package com.stock.controlador;

import com.stock.entidades.Gasto;
import com.stock.entidades.servicio.GastoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class GastoControlador {

    @Autowired
    private GastoService gastoService;

    @GetMapping("/listarGastos")
    public String listarGastos(Model model) {
        List<Gasto> gastos = gastoService.listarTodos();
        model.addAttribute("titulo", "Lista de Gastos");
        model.addAttribute("gastos", gastos);
        return "listarGastos";
    }

    @GetMapping("/gastoForm")
    public String nuevoGasto(Model model) {
        model.addAttribute("titulo", "Nuevo Gasto");
        model.addAttribute("gasto", new Gasto());
        return "gastoForm";
    }

    @GetMapping("/gastoForm/{id}")
    public String editarGasto(@PathVariable Long id, Model model, RedirectAttributes flash) {
        Gasto gasto = gastoService.findById(id);
        if (gasto == null) {
            flash.addFlashAttribute("error", "El gasto no existe.");
            return "redirect:/listarGastos";
        }
        model.addAttribute("titulo", "Editar Gasto");
        model.addAttribute("gasto", gasto);
        return "gastoForm";
    }

    @PostMapping("/guardarGasto")
    public String guardarGasto(@ModelAttribute Gasto gasto, RedirectAttributes flash) {
        gastoService.save(gasto);
        flash.addFlashAttribute("success", "Gasto guardado correctamente.");
        return "redirect:/listarGastos";
    }

    @GetMapping("/eliminarGasto/{id}")
    public String eliminarGasto(@PathVariable Long id, RedirectAttributes flash) {
        gastoService.deleteById(id);
        flash.addFlashAttribute("success", "Gasto eliminado correctamente.");
        return "redirect:/listarGastos";
    }
}
