package com.stock.entidades.servicio;

import com.stock.entidades.Clientes;
import com.stock.entidades.Combo;
import com.stock.entidades.ListaPrecio;
import com.stock.entidades.Venta;
import com.stock.entidades.VentaDetalle;
import com.stock.entidades.Vino;
import com.stock.repositorio.ClientesRepository;
import com.stock.repositorio.ComboRepository;
import com.stock.repositorio.ListaPrecioRepository;
import com.stock.repositorio.UsuarioRepository;
import com.stock.repositorio.VentaRepository;
import com.stock.repositorio.VinoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VentaServiceImpl implements VentaService {

    @Autowired private VentaRepository ventaRepository;
    @Autowired private VinoRepository vinoRepository;
    @Autowired private ClientesRepository clientesRepository;
    @Autowired private ListaPrecioRepository listaPrecioRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ComboRepository comboRepository;
    @Autowired private StockService stockService;
    @Autowired private PrecioVinoService precioVinoService;

    // --- Consultas simples ---

    @Override
    @Transactional(readOnly = true)
    public List<Venta> findAll() {
        return ventaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> findByActivoTrue() {
        return ventaRepository.findByActivoTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> searchVentas(LocalDate fechaInicio, LocalDate fechaFin, String clienteNombre, Boolean activo) {
        return ventaRepository.searchVentas(fechaInicio, fechaFin, clienteNombre, activo);
    }

    @Override
    @Transactional(readOnly = true)
    public Venta findById(Long id) {
        Venta venta = ventaRepository.findById(id).orElse(null);
        if (venta != null) {
            venta.getDetalles().size();
        }
        return venta;
    }

    @Override
    @Transactional
    public void save(Venta venta) {
        ventaRepository.save(venta);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        ventaRepository.deleteById(id);
    }

    @Override
    public long countAll() {
        return ventaRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal sumTotalVentas() {
        BigDecimal result = ventaRepository.sumTotalVentas();
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public List<Venta> findTop5ByOrderByFechaDesc() {
        return ventaRepository.findTop5ByOrderByFechaDesc();
    }

    // --- Lógica de negocio ---

    @Override
    @Transactional
    public void procesarVenta(Long id, Long clienteId,
                              Long[] itemIds, Integer[] cantidades, Long[] listaPrecioItemIds,
                              Long[] comboIds, Integer[] comboCantidades,
                              LocalDate fecha, String usuarioNombre) {

        boolean hasItems = itemIds != null && itemIds.length > 0;
        boolean hasCombos = comboIds != null && comboIds.length > 0;

        if (!hasItems && !hasCombos) {
            throw new RuntimeException("La venta no puede estar vacía.");
        }

        Clientes cliente = clientesRepository.findById(clienteId).orElse(null);
        if (cliente == null) {
            throw new RuntimeException("Cliente no encontrado.");
        }

        ListaPrecio listaPrecioDefecto = listaPrecioRepository.findAll().stream().findFirst().orElse(null);
        if (listaPrecioDefecto == null && hasItems) {
            throw new RuntimeException("No hay listas de precios configuradas en el sistema.");
        }

        validarStock(itemIds, cantidades, listaPrecioItemIds, comboIds, comboCantidades, hasItems, hasCombos);

        Venta venta = id != null
                ? ventaRepository.findById(id).orElseThrow(() -> new RuntimeException("La venta no existe en la base de datos."))
                : new Venta();

        venta.setFecha(fecha != null ? fecha : LocalDate.now());
        venta.setCliente(cliente);
        venta.setListaPrecio(listaPrecioDefecto);
        if (usuarioNombre != null) {
            venta.setUsuario(usuarioRepository.findByUsuario(usuarioNombre));
        }
        venta.getDetalles().clear();

        BigDecimal total = BigDecimal.ZERO;
        total = total.add(procesarVinos(venta, itemIds, cantidades, listaPrecioItemIds, listaPrecioDefecto, hasItems));
        total = total.add(procesarCombos(venta, comboIds, comboCantidades, hasCombos));

        venta.setTotal(total);
        ventaRepository.save(venta);
    }

    private void validarStock(Long[] itemIds, Integer[] cantidades, Long[] listaPrecioItemIds,
                               Long[] comboIds, Integer[] comboCantidades,
                               boolean hasItems, boolean hasCombos) {
        Map<Long, Integer> demanda = new HashMap<>();

        if (hasItems) {
            for (int i = 0; i < itemIds.length; i++) {
                demanda.merge(itemIds[i], cantidades[i], Integer::sum);
            }
        }
        if (hasCombos) {
            for (int i = 0; i < comboIds.length; i++) {
                Combo combo = comboRepository.findById(comboIds[i]).orElse(null);
                if (combo != null) {
                    for (Vino v : combo.getVinos()) {
                        demanda.merge(v.getId(), comboCantidades[i], Integer::sum);
                    }
                }
            }
        }

        for (Map.Entry<Long, Integer> entry : demanda.entrySet()) {
            Vino vino = vinoRepository.findById(entry.getKey()).orElse(null);
            if (vino == null) continue;
            int disponible = stockService.getStockTotal(vino);
            if (disponible < entry.getValue()) {
                throw new RuntimeException("Stock insuficiente para '" + vino.getNombre()
                        + "'. Requerido: " + entry.getValue() + ", Disponible: " + disponible + ".");
            }
        }
    }

    private BigDecimal procesarVinos(Venta venta, Long[] itemIds, Integer[] cantidades,
                                      Long[] listaPrecioItemIds, ListaPrecio listaPrecioDefecto,
                                      boolean hasItems) {
        BigDecimal total = BigDecimal.ZERO;
        if (!hasItems) return total;

        for (int i = 0; i < itemIds.length; i++) {
            Vino vino = vinoRepository.findById(itemIds[i]).orElse(null);
            if (vino == null) continue;

            boolean isPromoSeis = false;
            ListaPrecio listaItem = listaPrecioDefecto;
            BigDecimal precioUnitario = BigDecimal.ZERO;

            if (listaPrecioItemIds != null && i < listaPrecioItemIds.length && listaPrecioItemIds[i] != null) {
                if (listaPrecioItemIds[i] == -1L) {
                    isPromoSeis = true;
                    listaItem = findCajaListaPrecio();
                    if (listaItem != null) {
                        BigDecimal precioCaja = precioVinoService.findPrecioByVinoAndListaPrecio(vino, listaItem);
                        if (precioCaja != null) {
                            precioUnitario = precioCaja.divide(BigDecimal.valueOf(vino.getCantVinosxcaja()), 2, RoundingMode.HALF_UP);
                        }
                    }
                } else {
                    ListaPrecio li = listaPrecioRepository.findById(listaPrecioItemIds[i]).orElse(null);
                    if (li != null) {
                        listaItem = li;
                        precioUnitario = precioVinoService.findPrecioByVinoAndListaPrecio(vino, listaItem);
                    }
                }
            } else if (listaItem != null) {
                precioUnitario = precioVinoService.findPrecioByVinoAndListaPrecio(vino, listaItem);
            }

            if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) == 0) {
                throw new RuntimeException("Precio no encontrado para '" + vino.getNombre()
                        + (listaItem != null ? "' en lista '" + listaItem.getNombre() + "'" : "'") + ".");
            }

            BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidades[i]));
            VentaDetalle detalle = new VentaDetalle();
            detalle.setVino(vino);
            detalle.setCantidad(cantidades[i]);
            detalle.setListaPrecio(listaItem);
            detalle.setPrecioUnitario(precioUnitario);
            detalle.setPromoSeis(isPromoSeis);
            detalle.setPrecioCaja(precioUnitario.multiply(BigDecimal.valueOf(vino.getCantVinosxcaja())));
            detalle.setSubtotal(subtotal);
            detalle.setVenta(venta);
            venta.getDetalles().add(detalle);
            total = total.add(subtotal);
        }
        return total;
    }

    private BigDecimal procesarCombos(Venta venta, Long[] comboIds, Integer[] comboCantidades, boolean hasCombos) {
        BigDecimal total = BigDecimal.ZERO;
        if (!hasCombos) return total;

        for (int i = 0; i < comboIds.length; i++) {
            Combo combo = comboRepository.findById(comboIds[i]).orElse(null);
            if (combo == null) continue;
            BigDecimal precioUnitario = BigDecimal.valueOf(combo.getPrecio());
            BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(comboCantidades[i]));
            VentaDetalle detalle = new VentaDetalle();
            detalle.setCombo(combo);
            detalle.setCantidad(comboCantidades[i]);
            detalle.setPrecioUnitario(precioUnitario);
            detalle.setPrecioCaja(precioUnitario);
            detalle.setSubtotal(subtotal);
            detalle.setVenta(venta);
            venta.getDetalles().add(detalle);
            total = total.add(subtotal);
        }
        return total;
    }

    private ListaPrecio findCajaListaPrecio() {
        return listaPrecioRepository.findAll().stream()
                .filter(l -> l.getNombre().toLowerCase().contains("caja"))
                .findFirst()
                .orElse(null);
    }

    @Override
    @Transactional
    public void entregarVenta(Long ventaId) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new RuntimeException("La venta no existe en la base de datos"));

        if (venta.getEntregado()) {
            throw new RuntimeException("La venta ya ha sido entregada");
        }

        for (VentaDetalle detalle : venta.getDetalles()) {
            if (detalle.getVino() != null) {
                int cantidadADescontar = detalle.getCantidad();
                if (!Boolean.TRUE.equals(detalle.getPromoSeis())
                        && detalle.getListaPrecio() != null
                        && Boolean.TRUE.equals(detalle.getListaPrecio().getEsPorCaja())) {
                    cantidadADescontar = detalle.getCantidad() * detalle.getVino().getCantVinosxcaja();
                }
                stockService.discountStock(detalle.getVino(), cantidadADescontar);
            } else if (detalle.getCombo() != null) {
                for (Vino v : detalle.getCombo().getVinos()) {
                    stockService.discountStock(v, detalle.getCantidad());
                }
            }
        }

        venta.setEntregado(true);
        ventaRepository.save(venta);
    }
}
