---
name: Project Overview
description: Stack, dependencies, arquitectura y estado de configuración del proyecto stock-vinos
type: project
---

Spring Boot 2.6.4, Java 11, MySQL. App: gestión de stock y ventas de vinos (VendemosVinos).
Target deployment: Railway con MySQL plugin, perfil de producción separado en application-prod.properties.

## Estado actual de configuración (actualizado 2026-03-26)

application.properties (local/default):
- URL construida con variables de entorno Railway + fallback local: ${MYSQLHOST:localhost}:${MYSQLPORT:3306}/${MYSQLDATABASE:db_stock}
- ddl-auto=update (riesgo en producción si no se activa perfil prod)
- HikariCP configurado: pool-size=5, min-idle=2, connection-timeout=30000, idle-timeout=600000, max-lifetime=1800000, keepalive-time=60000, connection-test-query=SELECT 1
- socketTimeout=60000 y connectTimeout=30000 en la URL JDBC
- Nombres de variables CORRECTOS: MYSQLHOST, MYSQLPORT, MYSQLDATABASE, MYSQLUSER, MYSQLPASSWORD (coinciden con lo que Railway inyecta)

application-prod.properties (Railway):
- Variables sin fallback (correcto para producción)
- ddl-auto=validate (correcto para producción)
- HikariCP idéntico + leak-detection-threshold=60000
- socketTimeout en URL JDBC: AUSENTE (riesgo de hilos colgados)

railway.toml:
- Builder: Nixpacks
- buildCommand: mvn clean package -DskipTests
- startCommand: java -Djava.net.preferIPv6Addresses=true -Djava.net.preferIPv4Stack=false -jar target/stock-ventas-1.0.jar
- CRÍTICO: No activa --spring.profiles.active=prod → usa application.properties con ddl-auto=update en producción
- Las flags IPv6 fueron añadidas como intento de corregir el crash (commit c342f1d)

Driver MySQL:
- Artefacto: mysql-connector-java sin versión explícita → hereda de Spring Boot 2.6.4 BOM → versión 8.0.28
- GroupId: mysql (antiguo) en lugar de com.mysql (nuevo). Funcional pero desactualizado.

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

**Why:** Auditoría exhaustiva realizada 2026-03-25 / 2026-03-26.
**How to apply:** Priorizar corrección de race condition en entrega de ventas y N+1 en listado de vinos antes del deploy a Railway.
