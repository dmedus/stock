package com.stock.entidades.servicio;

import com.stock.controlador.dto.InformesMesDTO;
import com.stock.controlador.dto.TopVinoMesDTO;
import com.stock.entidades.Venta;
import com.stock.entidades.VentaDetalle;
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

    @Override
    @Transactional(readOnly = true)
    public InformesMesDTO getInformesMes(int anio, int mes) {
        LocalDate inicio = LocalDate.of(anio, mes, 1);
        LocalDate fin    = inicio.withDayOfMonth(inicio.lengthOfMonth());

        // Cargar ventas activas del mes con sus detalles
        List<Venta> ventas = ventaRepository.searchVentas(inicio, fin, null, true);
        ventas.forEach(v -> v.getDetalles().size()); // forzar lazy load

        InformesMesDTO dto = new InformesMesDTO();
        dto.setAnio(anio);
        dto.setMes(mes);
        dto.setNombreMes(NOMBRES_MES[mes]);

        // ---- Resumen de ventas ----
        BigDecimal totalIngresos = ventas.stream()
                .map(v -> v.getTotal() != null ? v.getTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCobrado = ventas.stream()
                .map(v -> v.getPagado() != null ? v.getPagado() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long cantidadVentas    = ventas.size();
        long ventasEntregadas  = ventas.stream().filter(v -> Boolean.TRUE.equals(v.getEntregado())).count();
        long clientesUnicos    = ventas.stream()
                .filter(v -> v.getCliente() != null)
                .map(v -> v.getCliente().getId())
                .distinct().count();

        dto.setTotalIngresos(totalIngresos);
        dto.setTotalCobrado(totalCobrado);
        dto.setTotalPorCobrar(totalIngresos.subtract(totalCobrado));
        dto.setCantidadVentas(cantidadVentas);
        dto.setVentasEntregadas(ventasEntregadas);
        dto.setVentasPendientesEntrega(cantidadVentas - ventasEntregadas);
        dto.setClientesUnicos(clientesUnicos);
        dto.setTicketPromedio(cantidadVentas > 0
                ? totalIngresos.divide(BigDecimal.valueOf(cantidadVentas), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO);

        // ---- Detalles de vino (excluye combos) ----
        List<VentaDetalle> detallesVino = ventas.stream()
                .flatMap(v -> v.getDetalles().stream())
                .filter(d -> d.getVino() != null)
                .collect(Collectors.toList());

        int totalBotellas = detallesVino.stream()
                .mapToInt(d -> d.getCantidad() != null ? d.getCantidad() : 0)
                .sum();
        dto.setTotalBotellas(totalBotellas);

        // ---- Rentabilidad ----
        BigDecimal costoTotal = detallesVino.stream()
                .map(d -> {
                    int cant = d.getCantidad() != null ? d.getCantidad() : 0;
                    BigDecimal cc = d.getVino().getCostoCompra() != null ? d.getVino().getCostoCompra() : BigDecimal.ZERO;
                    BigDecimal cf = d.getVino().getCostoFlete()  != null ? d.getVino().getCostoFlete()  : BigDecimal.ZERO;
                    return cc.add(cf).multiply(BigDecimal.valueOf(cant));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal subtotalVinos = detallesVino.stream()
                .map(d -> d.getSubtotal() != null ? d.getSubtotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal gananciaBruta = subtotalVinos.subtract(costoTotal);
        dto.setCostoTotal(costoTotal);
        dto.setGananciaBruta(gananciaBruta);

        if (costoTotal.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal margen = gananciaBruta
                    .divide(costoTotal, 4, RoundingMode.HALF_UP)
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
            Long vinoId  = d.getVino().getId();
            int  cant    = d.getCantidad() != null ? d.getCantidad() : 0;
            BigDecimal sub = d.getSubtotal() != null ? d.getSubtotal() : BigDecimal.ZERO;
            BigDecimal cc  = d.getVino().getCostoCompra() != null ? d.getVino().getCostoCompra() : BigDecimal.ZERO;
            BigDecimal cf  = d.getVino().getCostoFlete()  != null ? d.getVino().getCostoFlete()  : BigDecimal.ZERO;
            BigDecimal costo = cc.add(cf).multiply(BigDecimal.valueOf(cant));

            nombreMap.putIfAbsent(vinoId, d.getVino().getNombre());
            botellasMap.merge(vinoId, new long[]{cant}, (a, b) -> new long[]{a[0] + b[0]});
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
        return dto;
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
