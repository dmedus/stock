package com.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DatabaseFixer {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void fixDatabase() {
        try {
            System.out.println("--- INTENTANDO CORREGIR TABLA venta_detalle ---");
            // Alterar la columna vino_id para permitir NULL
            jdbcTemplate.execute("ALTER TABLE venta_detalle MODIFY COLUMN vino_id BIGINT NULL");
            System.out.println("--- TABLA venta_detalle CORREGIDA EXITOSAMENTE ---");
        } catch (Exception e) {
            System.out.println("--- ERROR AL CORREGIR TABLA (Puede que ya esté corregida o error de sintaxis): " + e.getMessage());
        }
    }
}
