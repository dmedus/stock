# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build
mvn clean package

# Run (requires MySQL running)
mvn spring-boot:run

# Run tests
mvn test

# Run a single test class
mvn test -Dtest=NombreDeClaseTest
```

## Database Setup

- MySQL database named `db_stock` on `localhost:3306`
- Local credentials: `root / root` (configured in `application.properties`)
- Schema is auto-managed via `spring.jpa.hibernate.ddl-auto=update`
- On first startup, `DataInitializer` seeds a default admin user: `admin / admin`
- The commented-out Railway connection string in `application.properties` is for production deployment

## Architecture Overview

Standard Spring Boot MVC application with Thymeleaf templates and Spring Security.

**Package structure** (`com.stock`):
- `entidades/` — JPA entities (no Lombok; plain getters/setters)
- `entidades/servicio/` — Service interfaces and their `*Impl` implementations
- `repositorio/` — Spring Data JPA repositories
- `controlador/` — Spring MVC controllers (Thymeleaf views)
- `utils/reporte/` — PDF (OpenPDF) and Excel (Apache POI) exporters
- `utils/paginacion/` — Custom pagination helpers (`PageRender`, `PageItem`)

**Core domain model:**
- `Vino` → belongs to `Bodega` (winery) and `Variedad` (grape variety); stock tracked in bottles
- `Stock` → tracks quantity of a `Vino` per `Deposito` (warehouse location)
- `Venta` → sale with `VentaDetalle` lines, linked to `Cliente`, `Usuario`, and `ListaPrecio`
- `PrecioVino` → price of a wine under a specific `ListaPrecio` (price list)
- `Pedido` / `PedidoDetalle` — purchase orders (ADMIN-only)
- `Combo` — bundle products

**Security:**
- Spring Security with `BCryptPasswordEncoder`
- Two roles: `ADMIN` and (implicit) authenticated user
- ADMIN-only routes: `/listarUsuarios`, `/usuarioForm/**`, `/eliminarUsuario/**`, `/listarPedidos`, `/pedidoForm/**`, `/eliminarPedido/**`, `/verPedidoDetalles/**`
- Public routes: `/js/**`, `/css/**`, `/img/**`, `/registro`, `/login`

**Key patterns:**
- Services use `@Transactional` (read-only where applicable)
- `Vino.stockActual` is a `@Transient` field populated at query time, not persisted
- `WebMvcConfig` configures static resource handling
- `DatabaseFixer` is a startup utility for one-time data corrections
