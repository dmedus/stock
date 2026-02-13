package com.stock.controlador;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.stock.entidades.Combo;
import com.stock.entidades.Vino;
import com.stock.repositorio.ComboRepository;
import com.stock.repositorio.VinoRepository;

@Controller
public class ComboControlador {

    @Autowired
    private ComboRepository comboRepository;

    @Autowired
    private VinoRepository vinoRepository;

    @GetMapping("/listarCombos")
    public String listarCombos(Model model) {
        model.addAttribute("combos", comboRepository.findAll());
        model.addAttribute("titulo", "Listado de Combos");
        return "listarCombos";
    }

    @GetMapping("/comboForm")
    public String comboForm(Model model) {
        model.addAttribute("combo", new com.stock.entidades.Combo());
        model.addAttribute("titulo", "Crear Combo");
        return "comboForm";
    }
    
    @GetMapping("/comboForm/{id}")
    public String comboForm(@PathVariable Long id, Model model) {
        Combo combo = comboRepository.findById(id).orElse(null);
        if (combo == null) {
            return "redirect:/listarCombos";
        }
        model.addAttribute("combo", combo);
        model.addAttribute("titulo", "Editar Combo");
        return "comboForm";
    }

    @PostMapping("/comboForm")
    public String guardarCombo(Combo combo, @RequestParam("vinos") List<Long> vinoIds) {
        List<Vino> vinos = vinoIds.stream()
                                  .map(id -> vinoRepository.findById(id).orElse(null))
                                  .filter(vino -> vino != null)
                                  .collect(Collectors.toList());
        combo.setVinos(vinos);
        comboRepository.save(combo);
        return "redirect:/listarCombos";
    }

    @GetMapping("/eliminarCombo/{id}")
    public String eliminarCombo(@PathVariable Long id) {
        comboRepository.deleteById(id);
        return "redirect:/listarCombos";
    }
}
