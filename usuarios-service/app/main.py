import os
import asyncio
from contextlib import asynccontextmanager

from dotenv import load_dotenv
from fastapi import FastAPI
from starlette.middleware.sessions import SessionMiddleware   # requerido por Authlib

from py_eureka_client import eureka_client

# ── Rutas existentes ───────────────────────────────────────────────────────────
from app.routes import auth_routes
from app.routes.oauth_routes import router as oauth_router

# ── Capa application (CQRS) ───────────────────────────────────────────────────
from app.application.queries.usuario_queries import (
    VerificarUsuarioQuery, VerificarUsuarioHandler,
    VerificarTutorMateriaQuery, VerificarTutorMateriaHandler,
    ObtenerNombreQuery, ObtenerNombreHandler,
)

# ── Infraestructura ───────────────────────────────────────────────────────────
from app.infrastructure.adapters.mongo_usuario_repository import MongoUsuarioRepository
from app.database import usuarios_collection

load_dotenv()

# ── Repositorio compartido ────────────────────────────────────────────────────
repo = MongoUsuarioRepository(usuarios_collection)

# ── Handlers de query (lado Read del CQRS) ───────────────────────────────────
verificar_handler      = VerificarUsuarioHandler(repo)
tutor_materia_handler  = VerificarTutorMateriaHandler(repo)
nombre_handler         = ObtenerNombreHandler(repo)

# ── Eureka ────────────────────────────────────────────────────────────────────
EUREKA_SERVER = os.getenv("EUREKA_SERVER", "http://localhost:8761/eureka")
APP_NAME      = "usuarios-service"
INSTANCE_PORT = 8000


async def init_eureka():
    try:
        await eureka_client.init_async(
            eureka_server=EUREKA_SERVER,
            app_name=APP_NAME,
            instance_port=INSTANCE_PORT,
        )
        print("Registrado en Eureka")
    except Exception as e:
        print(f"Eureka no disponible: {e}")


# ── App ───────────────────────────────────────────────────────────────────────
app = FastAPI(
    title="Microservicio de Usuarios",
    description="Arquitectura Hexagonal + DDD + CQRS + OAuth2 Google",
    version="2.0.0",
)

# SessionMiddleware es obligatorio para que Authlib (OAuth2) guarde el state
app.add_middleware(
    SessionMiddleware,
    secret_key=os.getenv("SECRET_KEY", "tu_clave_super_secreta_12345"),
)

# ── Routers ───────────────────────────────────────────────────────────────────
app.include_router(auth_routes.router)   # /auth/register, /auth/login, /auth/me, /auth/actualizar-materias
app.include_router(oauth_router)         # /auth/google/login, /auth/google/callback


# ── Startup ───────────────────────────────────────────────────────────────────
@app.on_event("startup")
async def startup_event():
    asyncio.create_task(init_eureka())


# ── Endpoints para Nicolás (reservas-service) — ahora via Query handlers ──────
@app.get("/api/usuarios/{usuario_id}/existe", tags=["Integración interna"])
async def verificar_usuario_existe(usuario_id: str):
    query = VerificarUsuarioQuery(usuario_id=usuario_id)
    return await verificar_handler.handle(query)


@app.get("/api/tutores/{tutor_id}/tiene-materia", tags=["Integración interna"])
async def verificar_tutor_materia(tutor_id: str, materiaId: str):
    query = VerificarTutorMateriaQuery(tutor_id=tutor_id, materia_id=materiaId)
    return await tutor_materia_handler.handle(query)


@app.get("/api/usuarios/{usuario_id}/nombre", tags=["Integración interna"])
async def obtener_nombre_usuario(usuario_id: str):
    query = ObtenerNombreQuery(usuario_id=usuario_id)
    nombre = await nombre_handler.handle(query)
    return {"nombre": nombre}


# ── Health check ──────────────────────────────────────────────────────────────
@app.get("/health", tags=["Monitoreo"])
async def health():
    return {"status": "ok", "service": "usuarios-service"}
