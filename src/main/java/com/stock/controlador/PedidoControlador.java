package com.stock.controlador;

import com.stock.entidades.Pedido;
import com.stock.entidades.servicio.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.stock.entidades.Vino;
import com.stock.entidades.servicio.PedidoDetalleService;
import com.stock.entidades.servicio.VinoService;
import com.stock.entidades.servicio.DepositoService;
import java.math.BigDecimal;
import com.stock.entidades.PedidoDetalle;


import java.util.List;
import java.util.Map;

@Controller
public class PedidoControlador {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private VinoService vinoService;

    @Autowired
    private PedidoDetalleService pedidoDetalleService;

    @Autowired
    private DepositoService depositoService;


    @GetMapping("/listarPedidos")
    public String listarPedidos(Model model) {
        List<Pedido> pedidos = pedidoService.findAll();
        model.addAttribute("titulo", "Listado de Pedidos");
        model.addAttribute("pedidos", pedidos);
        return "listarPedidos";
    }

    @GetMapping("/pedidoForm")
    public String crearPedido(Map<String, Object> model) {
        Pedido pedido = new Pedido();
        model.put("pedido", pedido);
        model.put("titulo", "Formulario de Pedido");
        model.put("depositos", depositoService.findAll());
        return "pedidoForm";
    }

    @PostMapping("/pedidoForm")
    public String guardarPedido(@ModelAttribute("pedido") Pedido pedido, BindingResult result, Model model, 
                                @RequestParam(name = "item_id[]", required = false) Long[] itemIds,
                                @RequestParam(name = "precio[]", required = false) BigDecimal[] precios,
                                @RequestParam(name = "cantidad[]", required = false) Integer[] cantidades,
                                RedirectAttributes flash, SessionStatus status) {
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Formulario de Pedido");
            return "pedidoForm";
        }

        if (itemIds == null || itemIds.length == 0) {
            flash.addFlashAttribute("error", "El pedido no puede estar vacío");
            return "redirect:/pedidoForm";
        }

        Pedido pedidoAGuardar;
        if (pedido.getId() != null) { // If it's an update
            pedidoAGuardar = pedidoService.findById(pedido.getId());
            if (pedidoAGuardar != null) {
                pedidoAGuardar.setFecha(pedido.getFecha());
                pedidoAGuardar.setProveedor(pedido.getProveedor());
                pedidoAGuardar.setObservaciones(pedido.getObservaciones());
                pedidoAGuardar.setPagado(pedido.getPagado());
                pedidoAGuardar.setActivo(pedido.getActivo());
                pedidoAGuardar.setDeposito(pedido.getDeposito());
                pedidoAGuardar.getDetalles().clear();
            } else {
                pedidoAGuardar = pedido;
            }
        } else { // It's a new Pedido
            pedidoAGuardar = pedido;
        }

        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < itemIds.length; i++) {
            Vino vino = vinoService.findById(itemIds[i]);
            if (vino != null) {
                PedidoDetalle detalle = new PedidoDetalle();
                detalle.setVino(vino);
                detalle.setPrecioUnitario(precios[i]);
                BigDecimal precioCajaCalculado = precios[i].multiply(new BigDecimal(vino.getCantVinosxcaja()));
                detalle.setPrecioCaja(precioCajaCalculado);
                detalle.setCantidad(cantidades[i] * vino.getCantVinosxcaja()); // Total bottles = boxes * bottles_per_box
                detalle.setSubtotal(precioCajaCalculado.multiply(new BigDecimal(cantidades[i]))); // Subtotal = price_per_box * number_of_boxes
                detalle.setPedido(pedidoAGuardar);
                pedidoAGuardar.getDetalles().add(detalle);
                total = total.add(detalle.getSubtotal());
            }
        }
        pedidoAGuardar.setTotal(total);

        pedidoService.save(pedidoAGuardar);
        status.setComplete();
        flash.addFlashAttribute("success", "Pedido guardado con éxito");
        return "redirect:/listarPedidos";
    }

    @GetMapping("/pedidoForm/{id}")
    public String editarPedido(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {
        Pedido pedido = null;
        if (id > 0) {
            pedido = pedidoService.findById(id);
            if (pedido == null) {
                flash.addFlashAttribute("error", "El ID del pedido no existe en la base de datos");
                return "redirect:/listarPedidos";
            }
        } else {
            flash.addFlashAttribute("error", "El ID del pedido no puede ser cero");
            return "redirect:/listarPedidos";
        }
        model.put("pedido", pedido);
        model.put("titulo", "Editar Pedido");
        model.put("depositos", depositoService.findAll());
        return "pedidoForm";
    }

    @GetMapping("/eliminarPedido/{id}")
    public String eliminarPedido(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
        if (id > 0) {
            pedidoService.deleteById(id);
            flash.addFlashAttribute("success", "Pedido eliminado con éxito");
        }
        return "redirect:/listarPedidos";
    }

    @GetMapping("/confirmarPedido/{id}")
    public String confirmarPedido(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
        try {
            pedidoService.confirmarPedido(id);
            flash.addFlashAttribute("success", "Pedido confirmado con éxito y stock actualizado");
        } catch (RuntimeException e) {
            flash.addFlashAttribute("error", "Error al confirmar el pedido: " + e.getMessage());
        }
        return "redirect:/listarPedidos";
    }

    @GetMapping("/verPedidoDetalles/{id}")
    public String verPedidoDetalles(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash) {
        Pedido pedido = pedidoService.findById(id);
        if (pedido == null) {
            flash.addFlashAttribute("error", "El ID del pedido no existe en la base de datos");
            return "redirect:/listarPedidos";
        }
        model.addAttribute("titulo", "Detalles del Pedido: " + pedido.getId());
        model.addAttribute("pedido", pedido);
        model.addAttribute("detalles", pedido.getDetalles()); // Assuming getDetalles() is available in Pedido
        return "verPedidoDetalles";
    }

    @GetMapping("/pagarPedido/{id}")
    public String pagarPedido(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
        Pedido pedido = pedidoService.findById(id);
        if (pedido == null) {
            flash.addFlashAttribute("error", "El ID del pedido no existe en la base de datos");
            return "redirect:/listarPedidos";
        }

        pedido.setPagado(pedido.getTotal());
        pedido.setActivo(false); // Mark as inactive when fully paid
        pedidoService.save(pedido);

        flash.addFlashAttribute("success", "Pedido marcado como pagado con éxito");
        return "redirect:/listarPedidos";
    }
}
