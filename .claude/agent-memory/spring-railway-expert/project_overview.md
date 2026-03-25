---
name: Project Overview
description: Stack, dependencies, arquitectura y estado de configuración del proyecto stock-vinos
type: project
---

Spring Boot 2.6.4, Java 11, MySQL. App: gestión de stock y ventas de vinos (VendemosVinos).
Target deployment: Railway con MySQL plugin, perfil de producción separado en application-prod.properties.

## Estado actual de configuración (2026-03-25)

application.properties (local):
- Apunta a localhost:3306/db_stock con usuario root/root
- ddl-auto=update (peligroso en producción)
- Sin configuración HikariCP

application-prod.properties (Railway):
- Usa variables de entorno ${MYSQLHOST}, ${MYSQLPORT}, ${MYSQLDATABASE}, ${MYSQLUSER}, ${MYSQLPASSWORD}
- ddl-auto=validate (correcto para producción)
- HikariCP configurado: pool-size=5, min-idle=2, connection-timeout=30000, idle-timeout=600000, max-lifetime=1800000, keepalive-time=60000, connection-test-query=SELECT 1
- Falta: socketTimeout explícito en la URL JDBC y leakDetectionThreshold

## Hallazgos de arquitectura críticos identificados (2026-03-25)

1. RACE CONDITION SEVERA: El descuento de stock en entregarVenta (VentaControlador) ocurre FUERA de @Transactional.
   El método stockService.discountStock() es @Transactional individualmente, pero el loop en el controlador no tiene transacción envolvente.
   Dos solicitudes simultáneas pueden descontar el mismo stock dos veces.

2. N+1 MASIVO en VinoServiceImpl.findAll(): por cada vino hace 1 query a StockRepository.findByVino() → O(N) queries al cargar la lista.

3. Race condition adicional en discountStock: hace getStockTotal() (lectura) y luego save() (escritura) en dos operaciones separadas sin SELECT FOR UPDATE.

4. javax.* en lugar de jakarta.*: Proyecto usa javax.persistence.* — confirma que es Spring Boot 2.x (no 3.x). Compatible con Java 11.

5. Entidades sin equals/hashCode: ninguna entidad implementa equals/hashCode basado en id. Riesgo en colecciones y detección de cambios de Hibernate.

6. VentaDetalle.promoSeis nullable sin @Column(nullable=false): puede generar NPE en lógica de negocio.

7. Clientes.fechaAlta usa java.sql.Date en lugar de LocalDate.

8. Usuario.rol es enum Rol sin @Enumerated — Hibernate lo mapea como ordinal por defecto (frágil si se reordena el enum).

9. Bodega.getCantidadVinos() (@Transient) dispara lazy load de la colección vinos si se llama fuera de sesión.

10. MovimientoStockControlador: el movimiento de stock entre depósitos (discount origen + increment destino) no está envuelto en @Transactional a nivel de servicio.

**Why:** Auditoría exhaustiva realizada 2026-03-25.
**How to apply:** Priorizar corrección de race condition en entrega de ventas y N+1 en listado de vinos antes del deploy a Railway.
