package com.stock.controlador;

import com.stock.entidades.servicio.RentabilidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RentabilidadControlador {

    @Autowired
    private RentabilidadService rentabilidadService;

    @GetMapping("/rentabilidad")
    public String verRentabilidad(Model model) {
        model.addAttribute("titulo", "Rentabilidad por Vino");
        model.addAttribute("filas", rentabilidadService.getRentabilidad());
        model.addAttribute("gananciaTotal", rentabilidadService.getGananciaProyectadaTotal());
        return "rentabilidad";
    }
}
