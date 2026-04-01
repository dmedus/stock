package com.stock.entidades.servicio;

import com.stock.controlador.dto.InformesMesDTO;
import com.stock.controlador.dto.TopVinoMesDTO;
import com.stock.controlador.dto.UsuarioResumenMesDTO;
import com.stock.entidades.Gasto;
import com.stock.entidades.Venta;
import com.stock.entidades.VentaDetalle;
import com.stock.entidades.Pago;
import com.stock.repositorio.GastoRepository;
import com.stock.repositorio.PagoRepository;
import com.stock.repositorio.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InformesServiceImpl implements InformesService {

    private static final String[] NOMBRES_MES = {
        "", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    };

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private GastoRepository gastoRepository;

    @Override
    @Transactional(readOnly = true)
    public InformesMesDTO getInformesMes(int anio, int mes) {
        LocalDate inicio = LocalDate.of(anio, mes, 1);
        LocalDate fin    = inicio.withDayOfMonth(inicio.lengthOfMonth());

        // Cargar TODAS las ventas del mes (activas y completadas)
        List<Venta> ventas = ventaRepository.searchVentas(inicio, fin, null, null);
        ventas.forEach(v -> v.getDetalles().size()); // forzar lazy load

        InformesMesDTO dto = new InformesMesDTO();
        dto.setAnio(anio);
        dto.setMes(mes);
        dto.setNombreMes(NOMBRES_MES[mes]);

        // ---- Resumen de ventas ----
        BigDecimal totalIngresos = ventas.stream()
                .map(v -> v.getTotal() != null ? v.getTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Total cobrado desde venta.pagado (tiene datos históricos y actuales)
        BigDecimal totalCobrado = ventas.stream()
                .map(v -> v.getPagado() != null ? v.getPagado() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Desglose por método desde tabla Pago (cubre pagos registrados con el nuevo sistema)
        List<Pago> pagos = pagoRepository.findByFechaBetween(inicio, fin);
        BigDecimal cobradoEfectivo = pagos.stream()
                .filter(p -> "Efectivo".equalsIgnoreCase(p.getMetodoPago()))
                .map(Pago::getMonto).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal cobradoTransferencia = pagos.stream()
                .filter(p -> "Transferencia".equalsIgnoreCase(p.getMetodoPago()))
                .map(Pago::getMonto).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal cobradoTarjeta = BigDecimal.ZERO;

        // Ventas realizadas = entregadas Y pagadas (activo=false)
        long cantidadVentas   = ventas.stream()
                .filter(v -> Boolean.TRUE.equals(v.getEntregado()) && !Boolean.TRUE.equals(v.getActivo()))
                .count();
        long ventasEntregadas = ventas.stream().filter(v -> Boolean.TRUE.equals(v.getEntregado())).count();
        long clientesUnicos    = ventas.stream()
                .filter(v -> v.getCliente() != null)
                .map(v -> v.getCliente().getId())
                .distinct().count();

        dto.setTotalIngresos(totalIngresos);
        dto.setTotalCobrado(totalCobrado);
        dto.setCobradoEfectivo(cobradoEfectivo);
        dto.setCobradoTransferencia(cobradoTransferencia);
        dto.setCobradoTarjeta(cobradoTarjeta);
        dto.setTotalPorCobrar(totalIngresos.subtract(totalCobrado));
        dto.setCantidadVentas(cantidadVentas);
        dto.setVentasEntregadas(ventasEntregadas);
        dto.setVentasPendientesEntrega(cantidadVentas - ventasEntregadas);
        dto.setClientesUnicos(clientesUnicos);
        dto.setTicketPromedio(cantidadVentas > 0
                ? totalIngresos.divide(BigDecimal.valueOf(cantidadVentas), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO);

        // ---- Ventas realizadas (entregadas y pagadas) ----
        List<Venta> ventasRealizadas = ventas.stream()
                .filter(v -> Boolean.TRUE.equals(v.getEntregado()) && !Boolean.TRUE.equals(v.getActivo()))
                .collect(Collectors.toList());

        // Detalles de vinos de ventas realizadas
        List<VentaDetalle> detallesVino = ventasRealizadas.stream()
                .flatMap(v -> v.getDetalles().stream())
                .filter(d -> d.getVino() != null)
                .collect(Collectors.toList());

        int totalBotellas = detallesVino.stream()
                .mapToInt(this::resolverBotellas)
                .sum();
        dto.setTotalBotellas(totalBotellas);

        // DEBUG: El Enemigo Malbec y Nicasia
        System.out.println("=== DEBUG VINOS (mes=" + mes + " anio=" + anio + ") ===");
        for (VentaDetalle d : detallesVino) {
            String nombre = d.getVino().getNombre() != null ? d.getVino().getNombre().toLowerCase() : "";
            if (nombre.contains("enemigo") || nombre.contains("nicasia")) {
                int bots = resolverBotellas(d);
                BigDecimal pu = d.getPrecioUnitario() != null ? d.getPrecioUnitario() : BigDecimal.ZERO;
                BigDecimal subtotalLinea = d.getSubtotal() != null ? d.getSubtotal() : pu.multiply(BigDecimal.valueOf(d.getCantidad() != null ? d.getCantidad() : 0));
                BigDecimal cc = d.getVino().getCostoCompra() != null ? d.getVino().getCostoCompra() : BigDecimal.ZERO;
                BigDecimal cf = d.getVino().getCostoFlete()  != null ? d.getVino().getCostoFlete()  : BigDecimal.ZERO;
                BigDecimal costoLin = cc.add(cf).multiply(BigDecimal.valueOf(bots));
                String lista = d.getListaPrecio() != null ? d.getListaPrecio().getNombre() : "null";
                boolean esCaja = d.getListaPrecio() != null && Boolean.TRUE.equals(d.getListaPrecio().getEsPorCaja());
                System.out.printf("  [%s] ventaId=%d | lista=%-20s esCaja=%b | cant=%d -> botellas=%d | precioUnit=%s | subtotalBD=%s | costoUnit=%s | costo=%s%n",
                        d.getVino().getNombre(), d.getVenta().getId(), lista, esCaja,
                        d.getCantidad() != null ? d.getCantidad() : 0, bots,
                        pu, subtotalLinea, cc.add(cf), costoLin);
            }
        }
        System.out.println("=== FIN DEBUG ===");

        // Ingreso = subtotal guardado en cada línea (cant × precioUnitario al momento de la venta).
        // Es la fuente más confiable independientemente de si precioUnitario es por botella o por caja.
        BigDecimal ingresoRealizadas = detallesVino.stream()
                .map(d -> d.getSubtotal() != null ? d.getSubtotal()
                        : (d.getPrecioUnitario() != null && d.getCantidad() != null
                                ? d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad()))
                                : BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // ---- Rentabilidad ----
        // costoCompra es por botella. Si la venta fue por caja, resolverBotellas
        // convierte cant (cajas) a botellas físicas para el cálculo correcto.
        BigDecimal costoTotal = detallesVino.stream()
                .map(d -> {
                    int botellas = resolverBotellas(d);
                    BigDecimal cc = d.getVino().getCostoCompra() != null ? d.getVino().getCostoCompra() : BigDecimal.ZERO;
                    BigDecimal cf = d.getVino().getCostoFlete()  != null ? d.getVino().getCostoFlete()  : BigDecimal.ZERO;
                    return cc.add(cf).multiply(BigDecimal.valueOf(botellas));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal gananciaBruta = ingresoRealizadas.subtract(costoTotal);
        dto.setSubtotalVinos(ingresoRealizadas);
        dto.setCostoTotal(costoTotal);
        dto.setGananciaBruta(gananciaBruta);

        if (ingresoRealizadas.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal margen = gananciaBruta
                    .divide(ingresoRealizadas, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(1, RoundingMode.HALF_UP);
            dto.setMargenBruto(margen);
        } else {
            dto.setMargenBruto(BigDecimal.ZERO);
        }

        // ---- Top 3 vinos ----
        // Acumular por vinoId: [botellas, subtotal, costo]
        Map<Long, long[]>       botellasMap  = new LinkedHashMap<>();
        Map<Long, BigDecimal[]> monetarioMap = new LinkedHashMap<>();
        Map<Long, String>       nombreMap    = new LinkedHashMap<>();

        for (VentaDetalle d : detallesVino) {
            Long vinoId    = d.getVino().getId();
            int  botellas  = resolverBotellas(d);
            BigDecimal sub = d.getSubtotal() != null ? d.getSubtotal()
                    : (d.getPrecioUnitario() != null && d.getCantidad() != null
                            ? d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad()))
                            : BigDecimal.ZERO);
            BigDecimal cc  = d.getVino().getCostoCompra() != null ? d.getVino().getCostoCompra() : BigDecimal.ZERO;
            BigDecimal cf  = d.getVino().getCostoFlete()  != null ? d.getVino().getCostoFlete()  : BigDecimal.ZERO;
            BigDecimal costo = cc.add(cf).multiply(BigDecimal.valueOf(botellas));

            nombreMap.putIfAbsent(vinoId, d.getVino().getNombre());
            botellasMap.merge(vinoId, new long[]{botellas}, (a, b) -> new long[]{a[0] + b[0]});
            monetarioMap.merge(vinoId,
                    new BigDecimal[]{sub, costo},
                    (a, b) -> new BigDecimal[]{a[0].add(b[0]), a[1].add(b[1])});
        }

        List<TopVinoMesDTO> topVinos = botellasMap.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue()[0], a.getValue()[0]))
                .limit(3)
                .map(e -> {
                    Long    id      = e.getKey();
                    int     bots    = (int) e.getValue()[0];
                    BigDecimal[] mon = monetarioMap.get(id);
                    return new TopVinoMesDTO(id, nombreMap.get(id), bots, mon[0], mon[1]);
                })
                .collect(Collectors.toList());

        dto.setTopVinos(topVinos);

        // ---- Resumen por usuario (ventas realizadas) ----
        Map<String, List<Venta>> porUsuario = ventasRealizadas.stream()
                .filter(v -> v.getUsuario() != null)
                .collect(Collectors.groupingBy(v -> v.getUsuario().getUsuario()));

        List<UsuarioResumenMesDTO> resumenUsuarios = porUsuario.entrySet().stream()
                .map(e -> buildResumenUsuario(e.getKey(), e.getValue()))
                .sorted((a, b) -> b.getIngresos().compareTo(a.getIngresos()))
                .collect(Collectors.toList());

        dto.setResumenUsuarios(resumenUsuarios);

        // ---- Gastos del mes ----
        List<Gasto> gastos = gastoRepository.findByFechaBetweenOrderByFechaDesc(inicio, fin);
        BigDecimal totalGastos = gastos.stream()
                .map(g -> g.getMonto() != null ? g.getMonto() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalGastos(totalGastos);
        dto.setGananciaNeta(gananciaBruta.subtract(totalGastos));

        return dto;
    }

    private UsuarioResumenMesDTO buildResumenUsuario(String nombreUsuario, List<Venta> ventasUsuario) {
        UsuarioResumenMesDTO u = new UsuarioResumenMesDTO();
        u.setUsuario(nombreUsuario);
        u.setVentasRealizadas((long) ventasUsuario.size());

        BigDecimal ingresos = ventasUsuario.stream()
                .map(v -> v.getTotal() != null ? v.getTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        u.setIngresos(ingresos);

        u.setTicketPromedio(!ventasUsuario.isEmpty()
                ? ingresos.divide(BigDecimal.valueOf(ventasUsuario.size()), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO);

        long clientesUnicos = ventasUsuario.stream()
                .filter(v -> v.getCliente() != null)
                .map(v -> v.getCliente().getId())
                .distinct().count();
        u.setClientesUnicos(clientesUnicos);

        List<VentaDetalle> detalles = ventasUsuario.stream()
                .flatMap(v -> v.getDetalles().stream())
                .filter(d -> d.getVino() != null)
                .collect(Collectors.toList());

        int botellas = detalles.stream().mapToInt(this::resolverBotellas).sum();
        u.setBotellas(botellas);

        BigDecimal costo = detalles.stream()
                .map(d -> {
                    int bots = resolverBotellas(d);
                    BigDecimal cc = d.getVino().getCostoCompra() != null ? d.getVino().getCostoCompra() : BigDecimal.ZERO;
                    BigDecimal cf = d.getVino().getCostoFlete()  != null ? d.getVino().getCostoFlete()  : BigDecimal.ZERO;
                    return cc.add(cf).multiply(BigDecimal.valueOf(bots));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        u.setCosto(costo);

        BigDecimal subtotal = detalles.stream()
                .map(d -> d.getSubtotal() != null ? d.getSubtotal()
                        : (d.getPrecioUnitario() != null && d.getCantidad() != null
                                ? d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad()))
                                : BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal ganancia = subtotal.subtract(costo);
        u.setGanancia(ganancia);

        if (subtotal.compareTo(BigDecimal.ZERO) > 0) {
            u.setMargen(ganancia.divide(subtotal, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(1, RoundingMode.HALF_UP));
        } else {
            u.setMargen(BigDecimal.ZERO);
        }

        return u;
    }

    /**
     * Devuelve la cantidad real de botellas de un detalle.
     * Usa el flag esPorCaja de ListaPrecio (mismo criterio que la pestaña Rentabilidad).
     */
    private int resolverBotellas(VentaDetalle d) {
        int cant = d.getCantidad() != null ? d.getCantidad() : 0;
        if (d.getListaPrecio() != null && Boolean.TRUE.equals(d.getListaPrecio().getEsPorCaja())) {
            int porCaja = (d.getVino() != null && d.getVino().getCantVinosxcaja() != null)
                    ? d.getVino().getCantVinosxcaja() : 1;
            return cant * porCaja;
        }
        return cant;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> getAniosDisponibles() {
        List<Integer> anios = new ArrayList<>();
        for (Object val : ventaRepository.findAniosConVentas()) {
            if (val != null) {
                anios.add(((Number) val).intValue());
            }
        }
        if (anios.isEmpty()) {
            anios.add(LocalDate.now().getYear());
        }
        return anios;
    }
}
