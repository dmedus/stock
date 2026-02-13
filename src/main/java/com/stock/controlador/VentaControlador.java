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

import com.stock.repositorio.ComboRepository;
import com.stock.entidades.Combo;
import java.util.HashMap;

@Controller
public class VentaControlador {

    @Autowired
    private ComboRepository comboRepository;

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

    @GetMapping(value = "/cargar-combos", produces = {"application/json"})
    public @ResponseBody List<Combo> cargarCombos(@RequestParam("term") String term) {
        return comboRepository.findByNombreContainingIgnoreCase(term);
    }

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
                               @RequestParam(required = false) Long clienteId,
                               @RequestParam(required = false) Long listaPrecioId,
                               @RequestParam(name = "item_id[]", required = false) Long[] itemIds,
                               @RequestParam(name = "cantidad[]", required = false) Integer[] cantidades,
                               @RequestParam(name = "lista_precio_item[]", required = false) Long[] listaPrecioItemIds,
                               @RequestParam(name = "combo_id[]", required = false) Long[] comboIds,
                               @RequestParam(name = "combo_cantidad[]", required = false) Integer[] comboCantidades,
                               RedirectAttributes flash, Model model, SessionStatus status, HttpServletRequest request) {

        System.out.println("--- INTENTANDO GUARDAR VENTA CON COMBOS Y PRECIOS VARIABLES ---");

        if (clienteId == null) {
             flash.addFlashAttribute("error", "Debe seleccionar un cliente.");
             return "redirect:/ventaForm";
        }
        // Removed global listaPrecioId validation

        boolean hasItems = (itemIds != null && itemIds.length > 0);
        boolean hasCombos = (comboIds != null && comboIds.length > 0);
        
        if (!hasItems && !hasCombos) {
             flash.addFlashAttribute("error", "La venta no puede estar vacía");
             return "redirect:/ventaForm";
        }

        Clientes cliente = clientesService.findById(clienteId);
        
        // Obtener una lista de precio por defecto (la primera) solo para referencias globales si fuera necesario
        // o si un item no trae su propia lista.
        ListaPrecio listaPrecioDefecto = listaPrecioService.findAll().stream().findFirst().orElse(null);

        if (cliente == null) {
            flash.addFlashAttribute("error", "Cliente no encontrado");
            return "redirect:/ventaForm";
        }
        
        if (listaPrecioDefecto == null && hasItems) {
             flash.addFlashAttribute("error", "No hay listas de precios configuradas en el sistema.");
             return "redirect:/ventaForm";
        }

        // --- VALIDACIÓN DE STOCK ACUMULADO ---
        Map<Long, Integer> demandaPorVino = new HashMap<>();

        // 1. Sumar demanda de vinos individuales
        if (hasItems) {
            for (int i = 0; i < itemIds.length; i++) {
                Long vinoId = itemIds[i];
                Integer cant = cantidades[i];
                demandaPorVino.put(vinoId, demandaPorVino.getOrDefault(vinoId, 0) + cant);
            }
        }

        // 2. Sumar demanda de vinos por combos
        if (hasCombos) {
            for (int i = 0; i < comboIds.length; i++) {
                Long comboId = comboIds[i];
                Integer cantCombo = comboCantidades[i];
                Combo combo = comboRepository.findById(comboId).orElse(null);
                if (combo != null) {
                    for (Vino v : combo.getVinos()) {
                        // Asumimos 1 botella por vino en la lista del combo
                        demandaPorVino.put(v.getId(), demandaPorVino.getOrDefault(v.getId(), 0) + cantCombo);
                    }
                }
            }
        }

        // 3. Verificar contra stock
        for (Map.Entry<Long, Integer> entry : demandaPorVino.entrySet()) {
            Vino v = vinoService.findById(entry.getKey());
            Integer stockActual = stockService.getStockTotal(v);
            if (stockActual < entry.getValue()) {
                model.addAttribute("error", "Stock insuficiente para '" + v.getNombre() + "'. Requerido: " + entry.getValue() + ", Disponible: " + stockActual);
                prepararModeloParaError(model, id, clienteId, null, itemIds, cantidades, listaPrecioItemIds, comboIds, comboCantidades);
                return "ventaForm";
            }
        }
        // -------------------------------------

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
        venta.setListaPrecio(listaPrecioDefecto); // Asignamos la por defecto por integridad, aunque ya no se use globalmente
        venta.getDetalles().clear();

        BigDecimal total = BigDecimal.ZERO;

        // Procesar Vinos Individuales
        if (hasItems) {
            for (int i = 0; i < itemIds.length; i++) {
                Vino vino = vinoService.findById(itemIds[i]);
                Integer cantidad = cantidades[i];
                
                // Determinar lista de precio para este item
                ListaPrecio listaItem = listaPrecioDefecto;
                if (listaPrecioItemIds != null && i < listaPrecioItemIds.length && listaPrecioItemIds[i] != null) {
                    ListaPrecio li = listaPrecioService.findById(listaPrecioItemIds[i]);
                    if (li != null) {
                        listaItem = li;
                    }
                }
                
                BigDecimal precioUnitario = precioVinoService.findPrecioByVinoAndListaPrecio(vino, listaItem);
                if (precioUnitario.compareTo(BigDecimal.ZERO) == 0) {
                    model.addAttribute("error", "Precio no encontrado para el vino: " + vino.getNombre() + " en lista " + listaItem.getNombre());
                    prepararModeloParaError(model, id, clienteId, null, itemIds, cantidades, listaPrecioItemIds, comboIds, comboCantidades);
                    return "ventaForm";
                }

                VentaDetalle detalle = new VentaDetalle();
                detalle.setVino(vino);
                detalle.setCantidad(cantidad);
                detalle.setListaPrecio(listaItem);
                detalle.setPrecioUnitario(precioUnitario);
                detalle.setPrecioCaja(precioUnitario.multiply(BigDecimal.valueOf(vino.getCantVinosxcaja()))); 
                BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
                detalle.setSubtotal(subtotal);
                detalle.setVenta(venta);
                venta.getDetalles().add(detalle);

                total = total.add(subtotal);
            }
        }

        // Procesar Combos
        if (hasCombos) {
            for (int i = 0; i < comboIds.length; i++) {
                Combo combo = comboRepository.findById(comboIds[i]).orElse(null);
                if (combo != null) {
                    Integer cantidad = comboCantidades[i];
                    VentaDetalle detalle = new VentaDetalle();
                    detalle.setCombo(combo);
                    detalle.setCantidad(cantidad);
                    // Para combos, usamos el precio del combo directo (o se podría calcular)
                    // Aquí asumimos que el precio del combo es fijo y global, o se ajusta por lista de precios si existiera lógica.
                    // Usaremos combo.getPrecio() como base.
                    BigDecimal precioUnitario = BigDecimal.valueOf(combo.getPrecio());
                    
                    detalle.setPrecioUnitario(precioUnitario);
                    detalle.setPrecioCaja(precioUnitario); // En combos no aplica "caja" igual, ponemos mismo precio
                    BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
                    detalle.setSubtotal(subtotal);
                    detalle.setVenta(venta);
                    venta.getDetalles().add(detalle);
                    
                    total = total.add(subtotal);
                }
            }
        }

        venta.setTotal(total);
        ventaService.save(venta);
        status.setComplete();
        flash.addFlashAttribute("success", "Venta guardada con éxito");
        return "redirect:/listarVentas";
    }

    private void prepararModeloParaError(Model model, Long ventaId, Long clienteId, Long listaPrecioId, Long[] itemIds, Integer[] cantidades, Long[] listaPrecioItemIds, Long[] comboIds, Integer[] comboCantidades) {
        Venta venta = new Venta();
        if (ventaId != null) {
            venta.setId(ventaId);
            venta.setFecha(ventaService.findById(ventaId).getFecha());
        } else {
             venta.setFecha(LocalDate.now());
        }

        if (clienteId != null) {
            venta.setCliente(clientesService.findById(clienteId));
        }
        
        ListaPrecio listaPrecioGlobal = null;
        if (listaPrecioId != null) {
             listaPrecioGlobal = listaPrecioService.findById(listaPrecioId);
             venta.setListaPrecio(listaPrecioGlobal);
        }
        
        // Reconstruir detalles de vinos
        if (itemIds != null && cantidades != null) {
             for(int i=0; i<itemIds.length; i++) {
                 Vino v = vinoService.findById(itemIds[i]);
                 if(v != null) {
                     VentaDetalle det = new VentaDetalle();
                     det.setVino(v);
                     det.setCantidad(cantidades[i]);
                     
                     // Reconstruir lista de precio por item
                     ListaPrecio listaItem = listaPrecioGlobal;
                     if (listaPrecioItemIds != null && i < listaPrecioItemIds.length && listaPrecioItemIds[i] != null) {
                         ListaPrecio li = listaPrecioService.findById(listaPrecioItemIds[i]);
                         if (li != null) {
                             listaItem = li;
                         }
                     }
                     det.setListaPrecio(listaItem);

                     if (listaItem != null) {
                         BigDecimal precio = precioVinoService.findPrecioByVinoAndListaPrecio(v, listaItem);
                         det.setPrecioUnitario(precio);
                         det.setSubtotal(precio.multiply(new BigDecimal(cantidades[i])));
                     }
                     venta.getDetalles().add(det);
                 }
             }
        }

        // Reconstruir detalles de combos
        if (comboIds != null && comboCantidades != null) {
            for(int i=0; i<comboIds.length; i++) {
                Combo c = comboRepository.findById(comboIds[i]).orElse(null);
                if(c != null) {
                    VentaDetalle det = new VentaDetalle();
                    det.setCombo(c);
                    det.setCantidad(comboCantidades[i]);
                    BigDecimal precio = BigDecimal.valueOf(c.getPrecio());
                    det.setPrecioUnitario(precio);
                    det.setSubtotal(precio.multiply(new BigDecimal(comboCantidades[i])));
                    venta.getDetalles().add(det);
                }
            }
        }
        
        model.addAttribute("venta", venta);
        model.addAttribute("listasPrecio", listaPrecioService.findAll());
        model.addAttribute("titulo", (ventaId != null) ? "Editar Venta" : "Crear Venta");
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
                if (detalle.getVino() != null) {
                    int cantidadADescontar = detalle.getCantidad();
                    
                    // Verificar si la venta fue por caja basándose en el nombre de la lista de precios
                    if (detalle.getListaPrecio() != null) {
                        String nombreLista = detalle.getListaPrecio().getNombre().toLowerCase();
                        if (nombreLista.contains("caja") || nombreLista.contains("mayorista") || nombreLista.contains("bulto")) {
                            cantidadADescontar = detalle.getCantidad() * detalle.getVino().getCantVinosxcaja();
                        }
                    }
                    
                    stockService.discountStock(detalle.getVino(), cantidadADescontar);
                } else if (detalle.getCombo() != null) {
                    Combo combo = detalle.getCombo();
                    Integer cantidadCombos = detalle.getCantidad();
                    // Iterate wines in the combo and discount based on combo quantity
                    for (Vino v : combo.getVinos()) {
                        stockService.discountStock(v, cantidadCombos);
                    }
                }
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
