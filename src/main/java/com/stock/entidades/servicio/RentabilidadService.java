package com.stock.entidades.servicio;

import com.stock.controlador.dto.RentabilidadVinoDTO;

import java.math.BigDecimal;
import java.util.List;

public interface RentabilidadService {
    List<RentabilidadVinoDTO> getRentabilidad();
    BigDecimal getGananciaProyectadaTotal();
}
