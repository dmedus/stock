package com.stock.entidades.servicio;

import com.stock.controlador.dto.InformesMesDTO;
import com.stock.controlador.dto.InformeVentasVendedorDTO;

import java.util.List;

public interface InformesService {

    InformesMesDTO getInformesMes(int anio, int mes);

    List<Integer> getAniosDisponibles();

    InformeVentasVendedorDTO getInformeVentasVendedor(int anio, int mes, Long usuarioId);
}
