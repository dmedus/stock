package com.stock.controlador.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class VentaResumenVendedorDTO {

    private Long ventaId;
    private LocalDate fecha;
    private String cliente;
    private String tipoVenta;
    private BigDecimal total;

    public VentaResumenVendedorDTO(Long ventaId, LocalDate fecha, String cliente, String tipoVenta, BigDecimal total) {
        this.ventaId = ventaId;
        this.fecha = fecha;
        this.cliente = cliente;
        this.tipoVenta = tipoVenta;
        this.total = total;
    }

    public Long getVentaId() { return ventaId; }
    public void setVentaId(Long ventaId) { this.ventaId = ventaId; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getTipoVenta() { return tipoVenta; }
    public void setTipoVenta(String tipoVenta) { this.tipoVenta = tipoVenta; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}
