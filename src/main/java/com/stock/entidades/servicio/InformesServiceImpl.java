package com.stock.entidades.servicio;

import com.stock.controlador.dto.InformesMesDTO;
import com.stock.controlador.dto.TopVinoMesDTO;
import com.stock.repositorio.VentaDetalleRepository;
import com.stock.repositorio.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class InformesServiceImpl implements InformesService {

    private static final String[] NOMBRES_MES = {
        "", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    };

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private VentaDetalleRepository ventaDetalleRepository;

    @Override
    @Transactional(readOnly = true)
    public InformesMesDTO getInformesMes(int anio, int mes) {
        InformesMesDTO dto = new InformesMesDTO();
        dto.setAnio(anio);
        dto.setMes(mes);
        dto.setNombreMes(NOMBRES_MES[mes]);

        // --- Resumen de ventas ---
        Object[] resumen = ventaRepository.getResumenMes(anio, mes);
        BigDecimal totalIngresos  = toBigDecimal(resumen[0]);
        BigDecimal totalCobrado   = toBigDecimal(resumen[1]);
        Long cantidadVentas       = toLong(resumen[2]);
        Long clientesUnicos       = toLong(resumen[3]);

        dto.setTotalIngresos(totalIngresos);
        dto.setTotalCobrado(totalCobrado);
        dto.setTotalPorCobrar(totalIngresos.subtract(totalCobrado));
        dto.setCantidadVentas(cantidadVentas);
        dto.setClientesUnicos(clientesUnicos);

        if (cantidadVentas > 0) {
            dto.setTicketPromedio(totalIngresos.divide(
                BigDecimal.valueOf(cantidadVentas), 2, RoundingMode.HALF_UP));
        } else {
            dto.setTicketPromedio(BigDecimal.ZERO);
        }

        // --- Operaciones ---
        Long entregadas = ventaRepository.countEntregadasMes(anio, mes);
        dto.setVentasEntregadas(entregadas);
        dto.setVentasPendientesEntrega(cantidadVentas - entregadas);

        // --- Botellas y costos ---
        Object[] totalesDetalle = ventaDetalleRepository.getTotalesDetallesMes(anio, mes);
        Integer totalBotellas = toInteger(totalesDetalle[0]);
        BigDecimal subtotalDetalle = toBigDecimal(totalesDetalle[1]);
        BigDecimal costoTotal = toBigDecimal(totalesDetalle[2]);

        dto.setTotalBotellas(totalBotellas);
        dto.setCostoTotal(costoTotal);
        dto.setGananciaBruta(subtotalDetalle.subtract(costoTotal));

        if (costoTotal.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal margen = dto.getGananciaBruta()
                    .divide(costoTotal, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(1, RoundingMode.HALF_UP);
            dto.setMargenBruto(margen);
        } else {
            dto.setMargenBruto(BigDecimal.ZERO);
        }

        // --- Top 3 vinos ---
        List<Object[]> topRows = ventaDetalleRepository.findTopVinosPorMes(anio, mes, PageRequest.of(0, 3));
        List<TopVinoMesDTO> topVinos = new ArrayList<>();
        for (Object[] row : topRows) {
            Long vinoId     = toLong(row[0]);
            String nombre   = (String) row[1];
            Integer botellas = toInteger(row[2]);
            BigDecimal ingresos = toBigDecimal(row[3]);
            BigDecimal costo    = toBigDecimal(row[4]);
            topVinos.add(new TopVinoMesDTO(vinoId, nombre, botellas, ingresos, costo));
        }
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

    // --- helpers de conversión ---

    private BigDecimal toBigDecimal(Object val) {
        if (val == null) return BigDecimal.ZERO;
        if (val instanceof BigDecimal) return (BigDecimal) val;
        return new BigDecimal(val.toString());
    }

    private Long toLong(Object val) {
        if (val == null) return 0L;
        if (val instanceof Long) return (Long) val;
        return ((Number) val).longValue();
    }

    private Integer toInteger(Object val) {
        if (val == null) return 0;
        if (val instanceof Integer) return (Integer) val;
        return ((Number) val).intValue();
    }
}
