package com.stock.controlador;

import com.stock.entidades.Venta;
import com.stock.entidades.VentaDetalle;
import com.stock.entidades.servicio.VentaService;
import com.stock.entidades.servicio.VentaDetalleService;
import org.springframework.web.bind.annotation.PathVariable;

import com.stock.entidades.Clientes;
import com.stock.entidades.ListaPrecio;
import com.stock.entidades.Pedido;
import com.stock.entidades.PedidoDetalle;
import com.stock.entidades.Vino;
import com.stock.entidades.servicio.ClientesService;
import com.stock.entidades.servicio.ListaPrecioService;
import com.stock.entidades.servicio.PedidoService;
import com.stock.entidades.servicio.PrecioVinoService;
import com.stock.entidades.servicio.VinoService;
import com.stock.entidades.servicio.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
public class VentaControlador {

    @Autowired
    private ClientesService clientesService;

    @Autowired
    private ListaPrecioService listaPrecioService;

    @Autowired
    private VinoService vinoService;

    @Autowired
    private PrecioVinoService precioVinoService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private StockService stockService;

    @GetMapping({"/ventaForm", "/ventaForm/{id}"})
    public String crearVenta(@PathVariable(name = "id", required = false) Long id, Map<String, Object> model, RedirectAttributes flash) {
        List<ListaPrecio> listasPrecio = listaPrecioService.findAll();

        if (id != null) {
            Venta venta = ventaService.findById(id);
            if (venta == null) {
                flash.addFlashAttribute("error", "El ID de la venta no existe en la base de datos");
                return "redirect:/listarVentas";
            }
            model.put("venta", venta);
            model.put("titulo", "Editar Venta");
        } else {
            Venta venta = new Venta();
            model.put("venta", venta);
            model.put("titulo", "Crear Venta");
        }

        model.put("listasPrecio", listasPrecio);

        return "ventaForm";
    }


    @GetMapping("/obtener-precio")
    @ResponseBody
    public BigDecimal obtenerPrecio(@RequestParam Long vinoId, @RequestParam Long listaPrecioId) {
        Vino vino = vinoService.findById(vinoId);
        ListaPrecio listaPrecio = listaPrecioService.findById(listaPrecioId);
        if (vino != null && listaPrecio != null) {
            return precioVinoService.findPrecioByVinoAndListaPrecio(vino, listaPrecio);
        }
        return BigDecimal.ZERO;
    }

    @Autowired
    private VentaService ventaService;

    @PostMapping({"/guardarVenta", "/guardarVenta/{id}"})
    public String guardarVenta(@PathVariable(name = "id", required = false) Long id,
                               @RequestParam Long clienteId,
                               @RequestParam Long listaPrecioId,
                               @RequestParam(name = "item_id[]", required = false) Long[] itemIds,
                               @RequestParam(name = "cantidad[]", required = false) Integer[] cantidades,
                               RedirectAttributes flash, SessionStatus status, HttpServletRequest request) {

        System.out.println(request.getParameterMap());

        if (itemIds == null || itemIds.length == 0) {
            flash.addFlashAttribute("error", "La venta no puede estar vacía");
            return "redirect:/ventaForm";
        }

        Clientes cliente = clientesService.findById(clienteId);
        ListaPrecio listaPrecio = listaPrecioService.findById(listaPrecioId);

        if (cliente == null || listaPrecio == null) {
            flash.addFlashAttribute("error", "Cliente o Lista de Precio no encontrados");
            return "redirect:/ventaForm";
        }

        Venta venta;
        if (id != null) {
            venta = ventaService.findById(id);
            if (venta == null) {
                flash.addFlashAttribute("error", "El ID de la venta no existe en la base de datos");
                return "redirect:/listarVentas";
            }
        } else {
            venta = new Venta();
            venta.setFecha(LocalDate.now());
        }

        venta.setCliente(cliente);
        venta.setListaPrecio(listaPrecio);

        // Clear existing details to replace them with the new ones
        venta.getDetalles().clear();

        BigDecimal total = BigDecimal.ZERO;

        for (int i = 0; i < itemIds.length; i++) {
            Long vinoId = itemIds[i];
            Integer cantidad = cantidades[i];

            Vino vino = vinoService.findById(vinoId);
            if (vino == null) {
                flash.addFlashAttribute("error", "Vino no encontrado con ID: " + vinoId);
                return "redirect:/ventaForm";
            }

            BigDecimal precioUnitario = precioVinoService.findPrecioByVinoAndListaPrecio(vino, listaPrecio);
            if (precioUnitario.compareTo(BigDecimal.ZERO) == 0) {
                flash.addFlashAttribute("error", "Precio no encontrado para el vino: " + vino.getNombre() + " en la lista de precios seleccionada.");
                return "redirect:/ventaForm";
            }

            VentaDetalle detalle = new VentaDetalle();
            detalle.setVino(vino);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(precioUnitario);
            detalle.setPrecioCaja(precioUnitario.multiply(BigDecimal.valueOf(vino.getCantVinosxcaja()))); // Assuming precioCaja is price per bottle * bottles per box
            BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
            detalle.setSubtotal(subtotal);
            detalle.setVenta(venta);
            venta.getDetalles().add(detalle);

            total = total.add(subtotal);
        }

        venta.setTotal(total);
        ventaService.save(venta);
        status.setComplete();
        flash.addFlashAttribute("success", "Venta guardada con éxito");
        return "redirect:/listarVentas";
    }

    @GetMapping("/listarVentas")
    public String listarVentas(Model model) {
        List<Venta> ventas = ventaService.findAll();
        model.addAttribute("titulo", "Listado de Ventas");
        model.addAttribute("ventas", ventas);
        return "listarVentas";
    }

    @Autowired
    private VentaDetalleService ventaDetalleService;

    @GetMapping("/verVentaDetalles/{id}")
    public String verVentaDetalles(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash) {
        Venta venta = ventaService.findById(id);
        if (venta == null) {
            flash.addFlashAttribute("error", "La venta no existe en la base de datos");
            return "redirect:/listarVentas";
        }

        model.addAttribute("venta", venta);
        model.addAttribute("titulo", "Detalles de la venta: " + venta.getId());
        return "verVentaDetalles";
    }

    @GetMapping("/eliminarVenta/{id}")
    public String eliminarVenta(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
        if (id > 0) {
            ventaService.deleteById(id);
            flash.addFlashAttribute("success", "Venta eliminada con éxito");
        }
        return "redirect:/listarVentas";
    }

    @PostMapping("/pagarVenta")
    public String pagarVenta(@RequestParam Long ventaId, @RequestParam String metodoPago, @RequestParam BigDecimal monto, RedirectAttributes flash) {
        Venta venta = ventaService.findById(ventaId);
        if (venta == null) {
            flash.addFlashAttribute("error", "La venta no existe en la base de datos");
            return "redirect:/listarVentas";
        }

        venta.setPagado(venta.getPagado().add(monto));

        if (venta.getPagado().compareTo(venta.getTotal()) >= 0) {
            venta.setActivo(false); // O alguna lógica para marcarla como completada
        }

        ventaService.save(venta);
        flash.addFlashAttribute("success", "Pago registrado con éxito");
        return "redirect:/listarVentas";
    }

    @PostMapping("/entregarVenta")
    public String entregarVenta(@RequestParam Long ventaId, RedirectAttributes flash) {
        Venta venta = ventaService.findById(ventaId);
        if (venta == null) {
            flash.addFlashAttribute("error", "La venta no existe en la base de datos");
            return "redirect:/listarVentas";
        }

        if (venta.getEntregado()) {
            flash.addFlashAttribute("warning", "La venta ya ha sido entregada");
            return "redirect:/listarVentas";
        }

        try {
            for (VentaDetalle detalle : venta.getDetalles()) {
                stockService.discountStock(detalle.getVino(), detalle.getCantidad());
            }

            venta.setEntregado(true);
            ventaService.save(venta);
            flash.addFlashAttribute("success", "Venta marcada como entregada. Stock actualizado.");
        } catch (RuntimeException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/listarVentas";
    }
}
