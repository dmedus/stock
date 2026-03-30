package com.stock.controlador.dto;

import java.math.BigDecimal;

public class RentabilidadVinoDTO {

    private Long vinoId;
    private String nombre;
    private String listaPrecio;
    private boolean esPorCaja;
    private BigDecimal precioVenta;
    private BigDecimal costoCompra;
    private BigDecimal costoFlete;
    private BigDecimal costoTotal;
    private BigDecimal ganancia;
    private BigDecimal margen;
    private Integer stockActual;
    private BigDecimal gananciaProyectada;

    public Long getVinoId() { return vinoId; }
    public void setVinoId(Long vinoId) { this.vinoId = vinoId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getListaPrecio() { return listaPrecio; }
    public void setListaPrecio(String listaPrecio) { this.listaPrecio = listaPrecio; }

    public boolean isEsPorCaja() { return esPorCaja; }
    public void setEsPorCaja(boolean esPorCaja) { this.esPorCaja = esPorCaja; }

    public BigDecimal getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(BigDecimal precioVenta) { this.precioVenta = precioVenta; }

    public BigDecimal getCostoCompra() { return costoCompra; }
    public void setCostoCompra(BigDecimal costoCompra) { this.costoCompra = costoCompra; }

    public BigDecimal getCostoFlete() { return costoFlete; }
    public void setCostoFlete(BigDecimal costoFlete) { this.costoFlete = costoFlete; }

    public BigDecimal getCostoTotal() { return costoTotal; }
    public void setCostoTotal(BigDecimal costoTotal) { this.costoTotal = costoTotal; }

    public BigDecimal getGanancia() { return ganancia; }
    public void setGanancia(BigDecimal ganancia) { this.ganancia = ganancia; }

    public BigDecimal getMargen() { return margen; }
    public void setMargen(BigDecimal margen) { this.margen = margen; }

    public Integer getStockActual() { return stockActual; }
    public void setStockActual(Integer stockActual) { this.stockActual = stockActual; }

    public BigDecimal getGananciaProyectada() { return gananciaProyectada; }
    public void setGananciaProyectada(BigDecimal gananciaProyectada) { this.gananciaProyectada = gananciaProyectada; }
}
