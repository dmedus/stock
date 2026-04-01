package com.stock.controlador.dto;

import java.math.BigDecimal;
import java.util.List;

public class InformesMesDTO {

    private int mes;
    private int anio;
    private String nombreMes;

    // Resumen de ventas
    private BigDecimal totalIngresos;
    private BigDecimal totalCobrado;
    private BigDecimal cobradoEfectivo;
    private BigDecimal cobradoTransferencia;
    private BigDecimal cobradoTarjeta;
    private BigDecimal totalPorCobrar;
    private Long cantidadVentas;
    private BigDecimal ticketPromedio;
    private Long clientesUnicos;

    // Operaciones
    private Integer totalBotellas;
    private Long ventasEntregadas;
    private Long ventasPendientesEntrega;

    // Rentabilidad
    private BigDecimal costoTotal;
    private BigDecimal gananciaBruta;
    private BigDecimal margenBruto;

    // Top vinos
    private List<TopVinoMesDTO> topVinos;

    public int getMes() { return mes; }
    public void setMes(int mes) { this.mes = mes; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public String getNombreMes() { return nombreMes; }
    public void setNombreMes(String nombreMes) { this.nombreMes = nombreMes; }

    public BigDecimal getTotalIngresos() { return totalIngresos; }
    public void setTotalIngresos(BigDecimal totalIngresos) { this.totalIngresos = totalIngresos; }

    public BigDecimal getTotalCobrado() { return totalCobrado; }
    public void setTotalCobrado(BigDecimal totalCobrado) { this.totalCobrado = totalCobrado; }

    public BigDecimal getCobradoEfectivo() { return cobradoEfectivo; }
    public void setCobradoEfectivo(BigDecimal cobradoEfectivo) { this.cobradoEfectivo = cobradoEfectivo; }

    public BigDecimal getCobradoTransferencia() { return cobradoTransferencia; }
    public void setCobradoTransferencia(BigDecimal cobradoTransferencia) { this.cobradoTransferencia = cobradoTransferencia; }

    public BigDecimal getCobradoTarjeta() { return cobradoTarjeta; }
    public void setCobradoTarjeta(BigDecimal cobradoTarjeta) { this.cobradoTarjeta = cobradoTarjeta; }

    public BigDecimal getTotalPorCobrar() { return totalPorCobrar; }
    public void setTotalPorCobrar(BigDecimal totalPorCobrar) { this.totalPorCobrar = totalPorCobrar; }

    public Long getCantidadVentas() { return cantidadVentas; }
    public void setCantidadVentas(Long cantidadVentas) { this.cantidadVentas = cantidadVentas; }

    public BigDecimal getTicketPromedio() { return ticketPromedio; }
    public void setTicketPromedio(BigDecimal ticketPromedio) { this.ticketPromedio = ticketPromedio; }

    public Long getClientesUnicos() { return clientesUnicos; }
    public void setClientesUnicos(Long clientesUnicos) { this.clientesUnicos = clientesUnicos; }

    public Integer getTotalBotellas() { return totalBotellas; }
    public void setTotalBotellas(Integer totalBotellas) { this.totalBotellas = totalBotellas; }

    public Long getVentasEntregadas() { return ventasEntregadas; }
    public void setVentasEntregadas(Long ventasEntregadas) { this.ventasEntregadas = ventasEntregadas; }

    public Long getVentasPendientesEntrega() { return ventasPendientesEntrega; }
    public void setVentasPendientesEntrega(Long ventasPendientesEntrega) { this.ventasPendientesEntrega = ventasPendientesEntrega; }

    public BigDecimal getCostoTotal() { return costoTotal; }
    public void setCostoTotal(BigDecimal costoTotal) { this.costoTotal = costoTotal; }

    public BigDecimal getGananciaBruta() { return gananciaBruta; }
    public void setGananciaBruta(BigDecimal gananciaBruta) { this.gananciaBruta = gananciaBruta; }

    public BigDecimal getMargenBruto() { return margenBruto; }
    public void setMargenBruto(BigDecimal margenBruto) { this.margenBruto = margenBruto; }

    public List<TopVinoMesDTO> getTopVinos() { return topVinos; }
    public void setTopVinos(List<TopVinoMesDTO> topVinos) { this.topVinos = topVinos; }
}
