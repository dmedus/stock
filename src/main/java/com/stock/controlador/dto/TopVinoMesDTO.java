package com.stock.controlador.dto;

import java.math.BigDecimal;

public class TopVinoMesDTO {

    private Long vinoId;
    private String nombre;
    private Integer botellas;
    private BigDecimal ingresos;
    private BigDecimal costo;
    private BigDecimal ganancia;
    private BigDecimal margen;

    public TopVinoMesDTO(Long vinoId, String nombre, Integer botellas, BigDecimal ingresos, BigDecimal costo) {
        this.vinoId = vinoId;
        this.nombre = nombre;
        this.botellas = botellas;
        this.ingresos = ingresos;
        this.costo = costo;
        this.ganancia = ingresos.subtract(costo);
        if (costo.compareTo(BigDecimal.ZERO) > 0) {
            this.margen = ganancia
                    .divide(costo, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(1, java.math.RoundingMode.HALF_UP);
        }
    }

    public Long getVinoId() { return vinoId; }
    public String getNombre() { return nombre; }
    public Integer getBotellas() { return botellas; }
    public BigDecimal getIngresos() { return ingresos; }
    public BigDecimal getCosto() { return costo; }
    public BigDecimal getGanancia() { return ganancia; }
    public BigDecimal getMargen() { return margen; }
}
