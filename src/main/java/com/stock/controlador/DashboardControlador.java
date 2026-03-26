package com.stock.controlador;

import com.stock.entidades.servicio.VentaService;
import com.stock.entidades.servicio.ClientesService;
import com.stock.entidades.servicio.VinoService;
import com.stock.entidades.servicio.PedidoService;
import com.stock.entidades.servicio.RentabilidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardControlador {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private ClientesService clientesService;

    @Autowired
    private VinoService vinoService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private RentabilidadService rentabilidadService;

    @GetMapping("/")
    public String dashboard(Model model, Authentication auth) {
        model.addAttribute("totalVentas", ventaService.countAll());
        model.addAttribute("totalIngresos", ventaService.sumTotalVentas());
        model.addAttribute("totalClientes", clientesService.countAll());
        model.addAttribute("totalVinos", vinoService.countAll());
        model.addAttribute("pedidosActivos", pedidoService.countActivePedidos());
        model.addAttribute("ventasRecientes", ventaService.findTop5ByOrderByFechaDesc());
        model.addAttribute("vinosFaltantes", vinoService.findVinosBelowMinStock());

        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            model.addAttribute("gananciaProyectada", rentabilidadService.getGananciaProyectadaTotal());
        }
        return "dashboard";
    }
}
