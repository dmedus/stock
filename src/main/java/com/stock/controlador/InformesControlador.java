package com.stock.controlador;

import com.stock.entidades.servicio.InformesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class InformesControlador {

    @Autowired
    private InformesService informesService;

    @GetMapping("/informes")
    public String verInformes(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio,
            Model model) {

        LocalDate hoy = LocalDate.now();
        int mesActual  = (mes  != null) ? mes  : hoy.getMonthValue();
        int anioActual = (anio != null) ? anio : hoy.getYear();

        List<Integer> aniosDisponibles = informesService.getAniosDisponibles();
        // Asegurar que el año actual siempre aparezca aunque no haya ventas
        if (!aniosDisponibles.contains(hoy.getYear())) {
            aniosDisponibles.add(0, hoy.getYear());
        }

        model.addAttribute("titulo", "Informes Mensuales");
        model.addAttribute("informe", informesService.getInformesMes(anioActual, mesActual));
        model.addAttribute("mesSeleccionado", mesActual);
        model.addAttribute("anioSeleccionado", anioActual);
        model.addAttribute("aniosDisponibles", aniosDisponibles);

        return "informes";
    }
}
