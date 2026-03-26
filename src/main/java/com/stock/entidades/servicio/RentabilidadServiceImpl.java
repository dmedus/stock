package com.stock.entidades.servicio;

import com.stock.controlador.dto.RentabilidadVinoDTO;
import com.stock.entidades.PrecioVino;
import com.stock.repositorio.PrecioVinoRepository;
import com.stock.repositorio.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RentabilidadServiceImpl implements RentabilidadService {

    @Autowired
    private PrecioVinoRepository precioVinoRepository;

    @Autowired
    private StockRepository stockRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RentabilidadVinoDTO> getRentabilidad() {
        Map<Long, Integer> stockMap = new HashMap<>();
        for (Object[] row : stockRepository.findStockTotalPorVino()) {
            Long vinoId = (Long) row[0];
            Integer total = ((Number) row[1]).intValue();
            stockMap.put(vinoId, total);
        }

        return precioVinoRepository.findAll().stream()
                .map(pv -> buildDTO(pv, stockMap))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getGananciaProyectadaTotal() {
        return getRentabilidad().stream()
                .map(RentabilidadVinoDTO::getGananciaProyectada)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private RentabilidadVinoDTO buildDTO(PrecioVino pv, Map<Long, Integer> stockMap) {
        RentabilidadVinoDTO dto = new RentabilidadVinoDTO();
        dto.setVinoId(pv.getVino().getId());
        dto.setNombre(pv.getVino().getNombre());
        dto.setListaPrecio(pv.getListaPrecio().getNombre());
        dto.setPrecioVenta(pv.getPrecio());

        BigDecimal costoCompra = pv.getVino().getCostoCompra() != null ? pv.getVino().getCostoCompra() : BigDecimal.ZERO;
        BigDecimal costoFlete  = pv.getVino().getCostoFlete()  != null ? pv.getVino().getCostoFlete()  : BigDecimal.ZERO;
        BigDecimal costoTotal  = costoCompra.add(costoFlete);

        dto.setCostoCompra(costoCompra);
        dto.setCostoFlete(costoFlete);
        dto.setCostoTotal(costoTotal);

        BigDecimal ganancia = pv.getPrecio().subtract(costoTotal);
        dto.setGanancia(ganancia);

        if (costoTotal.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal margen = ganancia
                    .divide(costoTotal, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(2, RoundingMode.HALF_UP);
            dto.setMargen(margen);
        }

        Integer stock = stockMap.getOrDefault(pv.getVino().getId(), 0);
        dto.setStockActual(stock);
        dto.setGananciaProyectada(ganancia.multiply(new BigDecimal(stock)));

        return dto;
    }
}
