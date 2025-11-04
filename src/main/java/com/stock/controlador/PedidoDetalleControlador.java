package com.stock.controlador;

import java.math.BigDecimal; // Added import
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.stock.entidades.Pedido;
import com.stock.entidades.PedidoDetalle;
import com.stock.entidades.Vino;
import com.stock.entidades.servicio.PedidoDetalleService;
import com.stock.entidades.servicio.PedidoService;
import com.stock.entidades.servicio.PrecioVinoService; // Added import
import com.stock.entidades.servicio.VinoService;




@Controller
public class PedidoDetalleControlador {

    @Autowired
    private PedidoDetalleService pedidoDetalleService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private VinoService vinoService;

    @Autowired
    private PrecioVinoService precioVinoService; // Added

    @GetMapping("/listarPedidoDetalles")
    public String listarPedidoDetalles(Model model) {
        List<PedidoDetalle> pedidoDetalles = pedidoDetalleService.findAll();
        model.addAttribute("titulo", "Listado de Detalles de Pedido");
        model.addAttribute("pedidoDetalles", pedidoDetalles);
        return "listarPedidoDetalles";
    }

    @GetMapping("/pedidoDetalleForm")
    public String crearPedidoDetalle(Map<String, Object> model) {
        PedidoDetalle pedidoDetalle = new PedidoDetalle();
        List<Pedido> pedidos = pedidoService.findActivePedidos();
        List<Vino> vinos = vinoService.findAll();
        model.put("pedidoDetalle", pedidoDetalle);
        model.put("pedidos", pedidos);
        model.put("vinos", vinos);
        model.put("titulo", "Formulario de Detalle de Pedido");
        return "pedidoDetalleForm";
    }

    @PostMapping("/pedidoDetalleForm")
    public String guardarPedidoDetalle(@ModelAttribute("pedidoDetalle") PedidoDetalle pedidoDetalle, BindingResult result, Model model, RedirectAttributes flash, SessionStatus status) {
        System.out.println("Entering guardarPedidoDetalle method."); // Added log
        if (result.hasErrors()) {
            System.out.println("Validation errors found."); // Added log
            List<Pedido> pedidos = pedidoService.findActivePedidos();
            List<Vino> vinos = vinoService.findAll();
            model.addAttribute("pedidos", pedidos);
            model.addAttribute("vinos", vinos);
            model.addAttribute("titulo", "Formulario de Detalle de Pedido");
            return "pedidoDetalleForm";
        }
        // Fetch Vino to get cantVinosxcaja and determine precioUnitario
        Vino selectedVino = vinoService.findById(pedidoDetalle.getVino().getId());
        if (selectedVino == null) {
            System.out.println("Vino not found."); // Added log
            flash.addFlashAttribute("error", "Vino no encontrado.");
            return "redirect:/pedidoDetalleForm";
        }

        // Use precioUnitario from the form
        if (pedidoDetalle.getPrecioUnitario() == null || pedidoDetalle.getPrecioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
            flash.addFlashAttribute("error", "El Precio Unitario debe ser mayor que cero.");
            return "redirect:/pedidoDetalleForm";
        }

        // Calculate precioCaja
        if (selectedVino.getCantVinosxcaja() != null && pedidoDetalle.getPrecioUnitario() != null) {
            pedidoDetalle.setPrecioCaja(pedidoDetalle.getPrecioUnitario().multiply(BigDecimal.valueOf(selectedVino.getCantVinosxcaja())));
        } else {
            System.out.println("Cannot calculate precioCaja."); // Added log
            flash.addFlashAttribute("error", "Precio Unitario o Cantidad de Vinos por Caja no disponibles para calcular Precio por Caja.");
            return "redirect:/pedidoDetalleForm";
        }

        // Calculate subtotal
        if (pedidoDetalle.getCantidad() != null && pedidoDetalle.getPrecioCaja() != null) {
            pedidoDetalle.setSubtotal(pedidoDetalle.getPrecioCaja().multiply(BigDecimal.valueOf(pedidoDetalle.getCantidad())));
        } else {
            System.out.println("Cannot calculate subtotal."); // Added log
            flash.addFlashAttribute("error", "Cantidad o Precio por Caja no disponibles para calcular el subtotal.");
            return "redirect:/pedidoDetalleForm";
        }

        pedidoDetalleService.save(pedidoDetalle);
        status.setComplete();
        flash.addFlashAttribute("success", "Detalle de Pedido guardado con éxito");
        System.out.println("PedidoDetalle saved successfully, redirecting."); // Added log
        return "redirect:/listarPedidoDetalles";
    }

    @GetMapping("/pedidoDetalleForm/{id}")
    public String editarPedidoDetalle(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {
        PedidoDetalle pedidoDetalle = null;
        if (id > 0) {
            pedidoDetalle = pedidoDetalleService.findById(id);
            if (pedidoDetalle == null) {
                flash.addFlashAttribute("error", "El ID del detalle de pedido no existe en la base de datos");
                return "redirect:/listarPedidoDetalles";
            }
        } else {
            flash.addFlashAttribute("error", "El ID del detalle de pedido no puede ser cero");
            return "redirect:/listarPedidoDetalles";
        }
        List<Pedido> pedidos = pedidoService.findActivePedidos();
        List<Vino> vinos = vinoService.findAll();
        model.put("pedidoDetalle", pedidoDetalle);
        model.put("pedidos", pedidos);
        model.put("vinos", vinos);
        model.put("titulo", "Editar Detalle de Pedido");
        return "pedidoDetalleForm";
    }

    @GetMapping("/eliminarPedidoDetalle/{id}")
    public String eliminarPedidoDetalle(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
        if (id > 0) {
            pedidoDetalleService.deleteById(id);
            flash.addFlashAttribute("success", "Detalle de Pedido eliminado con éxito");
        }
        return "redirect:/listarPedidoDetalles";
    }
}
