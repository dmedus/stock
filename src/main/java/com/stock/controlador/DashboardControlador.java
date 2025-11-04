package com.stock.controlador;

import com.stock.entidades.servicio.VentaService;
import com.stock.entidades.servicio.ClientesService;
import com.stock.entidades.servicio.VinoService;
import com.stock.entidades.servicio.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("totalVentas", ventaService.countAll());
        model.addAttribute("totalIngresos", ventaService.sumTotalVentas());
        model.addAttribute("totalClientes", clientesService.countAll());
        model.addAttribute("totalVinos", vinoService.countAll());
        model.addAttribute("pedidosActivos", pedidoService.countActivePedidos());
        model.addAttribute("ventasRecientes", ventaService.findTop5ByOrderByFechaDesc());
        model.addAttribute("vinosPocoStock", vinoService.findTop5ByOrderByStockActualAsc());
        return "dashboard";
    }
}
