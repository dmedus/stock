package com.stock.utils.reporte;

public enum Rol {
    USER("USER"),
    ADMIN("ADMIN"),
    EDITOR("EDITOR");

    private final String nombre;

    Rol(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}