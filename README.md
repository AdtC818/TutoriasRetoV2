# Plataforma de reservas de tutorías

Sistema para que los estudiantes de la UPTC reserven tutorías con docentes. Lo migramos a microservicios con arquitectura hexagonal, DDD y CQRS, comunicados por eventos con RabbitMQ.

## Servicios
| Servicio | Tecnología | Puerto |
|---|---|---|
| usuarios-service | Python / FastAPI | 8000 |
| catalogo-service | Node.js / Express | 3000 |
| reservas-service | Java / Spring Boot | 8082 |
| frontend | React + Vite | 5173 |
| api-gateway | Nginx | 8090 |
| RabbitMQ (panel) | RabbitMQ | 15672 |
| Prometheus / Grafana | Monitoreo | 9090 / 3001 |

Bases de datos: MongoDB, MySQL y PostgreSQL. Registro de servicios con Eureka.

## Cómo correrlo
Necesitas Docker Desktop. En la raíz del repo:

    cp .env.example .env
    docker compose up --build

Luego abre http://localhost:5173. El inicio de sesión con Google necesita credenciales propias en el .env; el resto funciona sin ellas.

## Mi aporte (Angel Torres, líder del equipo)
- Docker Compose del entorno completo y monitoreo con Prometheus y Grafana.
- Separación de comandos y consultas en el frontend (CQRS) y las vistas de registro, historial, rutas privadas y panel del tutor.
- Coordinación del equipo y de las entregas semanales.

## Equipo
JASH Solutions: Nicolas Parra, Laura Malagon, Elias Camargo