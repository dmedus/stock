package com.stock.controlador.dto;

import java.math.BigDecimal;

public class UsuarioResumenMesDTO {

    private String usuario;
    private Long ventasRealizadas;
    private Integer botellas;
    private BigDecimal ingresos;
    private BigDecimal costo;
    private BigDecimal ganancia;
    private BigDecimal margen;
    private BigDecimal ticketPromedio;
    private Long clientesUnicos;

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public Long getVentasRealizadas() { return ventasRealizadas; }
    public void setVentasRealizadas(Long ventasRealizadas) { this.ventasRealizadas = ventasRealizadas; }

    public Integer getBotellas() { return botellas; }
    public void setBotellas(Integer botellas) { this.botellas = botellas; }

    public BigDecimal getIngresos() { return ingresos; }
    public void setIngresos(BigDecimal ingresos) { this.ingresos = ingresos; }

    public BigDecimal getCosto() { return costo; }
    public void setCosto(BigDecimal costo) { this.costo = costo; }

    public BigDecimal getGanancia() { return ganancia; }
    public void setGanancia(BigDecimal ganancia) { this.ganancia = ganancia; }

    public BigDecimal getMargen() { return margen; }
    public void setMargen(BigDecimal margen) { this.margen = margen; }

    public BigDecimal getTicketPromedio() { return ticketPromedio; }
    public void setTicketPromedio(BigDecimal ticketPromedio) { this.ticketPromedio = ticketPromedio; }

    public Long getClientesUnicos() { return clientesUnicos; }
    public void setClientesUnicos(Long clientesUnicos) { this.clientesUnicos = clientesUnicos; }
}
