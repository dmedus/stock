package com.stock.controlador;

import com.stock.entidades.ListaPrecio;
import com.stock.entidades.servicio.ListaPrecioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
public class ListaPrecioControlador {

    @Autowired
    private ListaPrecioService listaPrecioService;

    @GetMapping("/listarListasPrecio")
    public String listarTiposListaPrecio(Model model) {
        List<ListaPrecio> listasPrecio = listaPrecioService.findAll();
        model.addAttribute("titulo", "Listado de Listas de Precio");
        model.addAttribute("listasPrecio", listasPrecio);
        return "tiposListaPrecio";
    }

    @GetMapping("/listaPrecioForm")
    public String crearListaPrecio(Map<String, Object> model) {
        ListaPrecio listaPrecio = new ListaPrecio();
        model.put("listaPrecio", listaPrecio);
        model.put("titulo", "Formulario de Lista de Precio");
        return "listaPrecioForm";
    }

    @PostMapping("/listaPrecioForm")
    public String guardarListaPrecio(@ModelAttribute("listaPrecio") ListaPrecio listaPrecio, BindingResult result, Model model, RedirectAttributes flash, SessionStatus status) {
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Formulario de Lista de Precio");
            return "listaPrecioForm";
        }
        listaPrecioService.save(listaPrecio);
        status.setComplete();
        flash.addFlashAttribute("success", "Lista de Precio guardada con éxito");
        return "redirect:/listarListasPrecio";
    }

    @GetMapping("/listaPrecioForm/{id}")
    public String editarListaPrecio(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {
        ListaPrecio listaPrecio = null;
        if (id > 0) {
            listaPrecio = listaPrecioService.findById(id);
            if (listaPrecio == null) {
                flash.addFlashAttribute("error", "El ID de la lista de precio no existe en la base de datos");
                return "redirect:/listarListasPrecio";
            }
        } else {
            flash.addFlashAttribute("error", "El ID de la lista de precio no puede ser cero");
            return "redirect:/listarListasPrecio";
        }
        model.put("listaPrecio", listaPrecio);
        model.put("titulo", "Editar Lista de Precio");
        return "listaPrecioForm";
    }

    @GetMapping("/eliminarListaPrecio/{id}")
    public String eliminarListaPrecio(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
        if (id > 0) {
            listaPrecioService.deleteById(id);
            flash.addFlashAttribute("success", "Lista de Precio eliminada con éxito");
        }
        return "redirect:/listarListasPrecio";
    }
}
