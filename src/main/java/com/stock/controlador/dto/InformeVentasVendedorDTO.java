package com.stock.controlador.dto;

import java.math.BigDecimal;
import java.util.List;

public class InformeVentasVendedorDTO {

    private int mes;
    private int anio;
    private String nombreMes;
    private Long usuarioId;
    private String usuarioNombre;

    private List<VentaResumenVendedorDTO> ventas;
    private List<ResumenTipoVentaDTO> resumenPorTipo;
    private BigDecimal totalGeneral;

    public int getMes() { return mes; }
    public void setMes(int mes) { this.mes = mes; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public String getNombreMes() { return nombreMes; }
    public void setNombreMes(String nombreMes) { this.nombreMes = nombreMes; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }

    public List<VentaResumenVendedorDTO> getVentas() { return ventas; }
    public void setVentas(List<VentaResumenVendedorDTO> ventas) { this.ventas = ventas; }

    public List<ResumenTipoVentaDTO> getResumenPorTipo() { return resumenPorTipo; }
    public void setResumenPorTipo(List<ResumenTipoVentaDTO> resumenPorTipo) { this.resumenPorTipo = resumenPorTipo; }

    public BigDecimal getTotalGeneral() { return totalGeneral; }
    public void setTotalGeneral(BigDecimal totalGeneral) { this.totalGeneral = totalGeneral; }
}
