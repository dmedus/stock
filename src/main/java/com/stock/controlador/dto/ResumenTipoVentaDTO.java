package com.stock.controlador.dto;

import java.math.BigDecimal;

public class ResumenTipoVentaDTO {

    private String tipoVenta;
    private Long cantidadVentas;
    private BigDecimal total;

    public ResumenTipoVentaDTO(String tipoVenta, Long cantidadVentas, BigDecimal total) {
        this.tipoVenta = tipoVenta;
        this.cantidadVentas = cantidadVentas;
        this.total = total;
    }

    public String getTipoVenta() { return tipoVenta; }
    public void setTipoVenta(String tipoVenta) { this.tipoVenta = tipoVenta; }

    public Long getCantidadVentas() { return cantidadVentas; }
    public void setCantidadVentas(Long cantidadVentas) { this.cantidadVentas = cantidadVentas; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}
