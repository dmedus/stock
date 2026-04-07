package com.stock.controlador;

import com.stock.entidades.Venta;
import com.stock.entidades.servicio.VentaService;
import com.stock.entidades.servicio.VentaDetalleService;
import com.stock.entidades.ListaPrecio;
import com.stock.entidades.Vino;
import com.stock.entidades.servicio.ListaPrecioService;
import com.stock.entidades.servicio.PrecioVinoService;
import com.stock.entidades.servicio.VinoService;
import com.stock.repositorio.ComboRepository;
import com.stock.repositorio.PagoRepository;
import com.stock.entidades.Combo;
import com.stock.entidades.Pago;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
public class VentaControlador {

    @Autowired private VentaService ventaService;
    @Autowired private VentaDetalleService ventaDetalleService;
    @Autowired private ListaPrecioService listaPrecioService;
    @Autowired private VinoService vinoService;
    @Autowired private PrecioVinoService precioVinoService;
    @Autowired private ComboRepository comboRepository;
    @Autowired private PagoRepository pagoRepository;

    // --- Formulario de venta ---

    @GetMapping({"/ventaForm", "/ventaForm/{id}"})
    public String crearVenta(@PathVariable(name = "id", required = false) Long id,
                             Map<String, Object> model, RedirectAttributes flash) {
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
            venta.setFecha(LocalDate.now());
            model.put("venta", venta);
            model.put("titulo", "Crear Venta");
        }

        model.put("listasPrecio", listasPrecio);
        return "ventaForm";
    }

    // --- Endpoints JSON ---

    @GetMapping(value = "/cargar-combos", produces = "application/json")
    public @ResponseBody List<Combo> cargarCombos(@RequestParam("term") String term) {
        return comboRepository.findByNombreContainingIgnoreCase(term);
    }

    @GetMapping("/obtener-precio")
    @ResponseBody
    public BigDecimal obtenerPrecio(@RequestParam Long vinoId, @RequestParam Long listaPrecioId) {
        Vino vino = vinoService.findById(vinoId);
        if (vino == null) return BigDecimal.ZERO;

        if (listaPrecioId == -1L) {
            ListaPrecio listaCaja = listaPrecioService.findAll().stream()
                    .filter(l -> l.getNombre().toLowerCase().contains("caja"))
                    .findFirst().orElse(null);
            if (listaCaja != null) {
                BigDecimal precioCaja = precioVinoService.findPrecioByVinoAndListaPrecio(vino, listaCaja);
                if (precioCaja != null && precioCaja.compareTo(BigDecimal.ZERO) > 0) {
                    return precioCaja.divide(BigDecimal.valueOf(vino.getCantVinosxcaja()), 2, java.math.RoundingMode.HALF_UP);
                }
            }
            return BigDecimal.ZERO;
        }

        ListaPrecio listaPrecio = listaPrecioService.findById(listaPrecioId);
        if (listaPrecio != null) {
            return precioVinoService.findPrecioByVinoAndListaPrecio(vino, listaPrecio);
        }
        return BigDecimal.ZERO;
    }

    // --- Guardar venta ---

    @PostMapping({"/guardarVenta", "/guardarVenta/{id}"})
    public String guardarVenta(@PathVariable(name = "id", required = false) Long id,
                               @RequestParam(required = false) Long clienteId,
                               @RequestParam(name = "item_id[]", required = false) Long[] itemIds,
                               @RequestParam(name = "cantidad[]", required = false) Integer[] cantidades,
                               @RequestParam(name = "lista_precio_item[]", required = false) Long[] listaPrecioItemIds,
                               @RequestParam(name = "combo_id[]", required = false) Long[] comboIds,
                               @RequestParam(name = "combo_cantidad[]", required = false) Integer[] comboCantidades,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                               Authentication authentication,
                               RedirectAttributes flash, SessionStatus status) {

        if (clienteId == null) {
            flash.addFlashAttribute("error", "Debe seleccionar un cliente.");
            return id != null ? "redirect:/ventaForm/" + id : "redirect:/ventaForm";
        }

        try {
            String usuarioNombre = authentication != null ? authentication.getName() : null;
            ventaService.procesarVenta(id, clienteId, itemIds, cantidades, listaPrecioItemIds,
                    comboIds, comboCantidades, fecha, usuarioNombre);
            status.setComplete();
            flash.addFlashAttribute("success", "Venta guardada con éxito");
            return "redirect:/listarVentas";
        } catch (RuntimeException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return id != null ? "redirect:/ventaForm/" + id : "redirect:/ventaForm";
        }
    }

    // --- Listado de ventas ---

    @GetMapping("/listarVentas")
    public String listarVentas(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                               @RequestParam(required = false) String cliente,
                               @RequestParam(required = false) Boolean activo,
                               @RequestParam(required = false) Boolean submitted,
                               Model model) {

        List<Venta> ventas = Boolean.TRUE.equals(submitted)
                ? ventaService.searchVentas(fechaInicio, fechaFin, cliente, activo)
                : ventaService.findByActivoTrue();

        model.addAttribute("titulo", "Listado de Ventas");
        model.addAttribute("ventas", ventas);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("cliente", cliente);
        model.addAttribute("activo", activo);
        return "listarVentas";
    }

    // --- Ver detalles ---

    @GetMapping("/verVentaDetalles/{id}")
    public String verVentaDetalles(@PathVariable Long id, Model model, RedirectAttributes flash) {
        Venta venta = ventaService.findById(id);
        if (venta == null) {
            flash.addFlashAttribute("error", "La venta no existe en la base de datos");
            return "redirect:/listarVentas";
        }
        model.addAttribute("venta", venta);
        model.addAttribute("titulo", "Detalles de la venta: " + venta.getId());
        return "verVentaDetalles";
    }

    // --- Eliminar ---

    @PostMapping("/eliminarVenta/{id}")
    public String eliminarVenta(@PathVariable Long id, RedirectAttributes flash) {
        if (id > 0) {
            ventaService.deleteById(id);
            flash.addFlashAttribute("success", "Venta eliminada con éxito");
        }
        return "redirect:/listarVentas";
    }

    // --- Pagar ---

    @PostMapping("/pagarVenta")
    public String pagarVenta(@RequestParam Long ventaId, @RequestParam String metodoPago,
                             @RequestParam BigDecimal monto, RedirectAttributes flash) {
        Venta venta = ventaService.findById(ventaId);
        if (venta == null) {
            flash.addFlashAttribute("error", "La venta no existe en la base de datos");
            return "redirect:/listarVentas";
        }
        if (!Boolean.TRUE.equals(venta.getActivo())) {
            flash.addFlashAttribute("error", "Esta venta ya fue completada y no admite más pagos.");
            return "redirect:/listarVentas";
        }

        venta.setPagado(venta.getPagado().add(monto));
        if (venta.getPagado().compareTo(venta.getTotal()) >= 0) {
            venta.setActivo(false);
        }
        ventaService.save(venta);

        Pago pago = new Pago();
        pago.setVenta(venta);
        pago.setMonto(monto);
        pago.setMetodoPago(metodoPago);
        pago.setFecha(LocalDate.now());
        pagoRepository.save(pago);

        flash.addFlashAttribute("success", "Pago registrado con éxito");
        return "redirect:/listarVentas";
    }

    // --- Entregar ---

    @PostMapping("/entregarVenta")
    public String entregarVenta(@RequestParam Long ventaId, RedirectAttributes flash) {
        try {
            ventaService.entregarVenta(ventaId);
            flash.addFlashAttribute("success", "Venta marcada como entregada. Stock actualizado.");
        } catch (RuntimeException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/listarVentas";
    }
}
