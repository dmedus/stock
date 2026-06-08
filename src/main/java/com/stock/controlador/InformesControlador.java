package com.stock.controlador;

import com.stock.entidades.Usuario;
import com.stock.entidades.servicio.InformesService;
import com.stock.entidades.servicio.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class InformesControlador {

    @Autowired
    private InformesService informesService;

    @Autowired
    private UsuarioService usuarioService;

    private static final Map<Integer, String> NOMBRES_MES = new LinkedHashMap<>();
    static {
        NOMBRES_MES.put(1,  "Enero");
        NOMBRES_MES.put(2,  "Febrero");
        NOMBRES_MES.put(3,  "Marzo");
        NOMBRES_MES.put(4,  "Abril");
        NOMBRES_MES.put(5,  "Mayo");
        NOMBRES_MES.put(6,  "Junio");
        NOMBRES_MES.put(7,  "Julio");
        NOMBRES_MES.put(8,  "Agosto");
        NOMBRES_MES.put(9,  "Septiembre");
        NOMBRES_MES.put(10, "Octubre");
        NOMBRES_MES.put(11, "Noviembre");
        NOMBRES_MES.put(12, "Diciembre");
    }

    @GetMapping("/informes")
    public String verInformes(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio,
            Model model) {

        LocalDate hoy = LocalDate.now();
        int mesActual  = (mes  != null) ? mes  : hoy.getMonthValue();
        int anioActual = (anio != null) ? anio : hoy.getYear();

        List<Integer> aniosDisponibles = informesService.getAniosDisponibles();
        if (!aniosDisponibles.contains(hoy.getYear())) {
            aniosDisponibles.add(0, hoy.getYear());
        }

        model.addAttribute("titulo", "Informes Mensuales");
        model.addAttribute("informe", informesService.getInformesMes(anioActual, mesActual));
        model.addAttribute("mesSeleccionado", mesActual);
        model.addAttribute("anioSeleccionado", anioActual);
        model.addAttribute("aniosDisponibles", aniosDisponibles);
        model.addAttribute("nombresMes", NOMBRES_MES);

        return "informes";
    }

    @GetMapping("/informes/vendedor")
    public String verInformeVendedor(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) Long usuarioId,
            Model model) {

        LocalDate hoy = LocalDate.now();
        int mesActual  = (mes  != null) ? mes  : hoy.getMonthValue();
        int anioActual = (anio != null) ? anio : hoy.getYear();

        List<Integer> aniosDisponibles = informesService.getAniosDisponibles();
        if (!aniosDisponibles.contains(hoy.getYear())) {
            aniosDisponibles.add(0, hoy.getYear());
        }

        List<Usuario> vendedores = usuarioService.listarUsuarios().stream()
                .filter(u -> Boolean.TRUE.equals(u.getActivo()))
                .collect(Collectors.toList());

        model.addAttribute("titulo", "Ventas por Vendedor");
        model.addAttribute("informeVendedor", informesService.getInformeVentasVendedor(anioActual, mesActual, usuarioId));
        model.addAttribute("mesSeleccionado", mesActual);
        model.addAttribute("anioSeleccionado", anioActual);
        model.addAttribute("usuarioSeleccionado", usuarioId);
        model.addAttribute("aniosDisponibles", aniosDisponibles);
        model.addAttribute("nombresMes", NOMBRES_MES);
        model.addAttribute("vendedores", vendedores);

        return "informeVentasVendedor";
    }
}
