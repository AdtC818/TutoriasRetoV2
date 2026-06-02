"""
Application layer — Queries (lectura / lado Read del CQRS).

VerificarUsuarioQuery  → ¿existe el usuario? ¿tiene la materia?
ObtenerNombreQuery     → devuelve el nombre del usuario
"""
from __future__ import annotations
from dataclasses import dataclass
from typing import Optional

from app.domain.usuario import UsuarioRepository


# ──────────────────────────────────────────────
# Query: Verificar existencia
# ──────────────────────────────────────────────
@dataclass
class VerificarUsuarioQuery:
    usuario_id: str   # correo o ObjectId


class VerificarUsuarioHandler:
    def __init__(self, repo: UsuarioRepository):
        self._repo = repo

    async def handle(self, query: VerificarUsuarioQuery) -> bool:
        usuario = await self._repo.buscar_por_correo(query.usuario_id)
        if usuario:
            return True
        usuario = await self._repo.buscar_por_id(query.usuario_id)
        return usuario is not None


# ──────────────────────────────────────────────
# Query: Verificar tutor con materia
# ──────────────────────────────────────────────
@dataclass
class VerificarTutorMateriaQuery:
    tutor_id: str
    materia_id: str


class VerificarTutorMateriaHandler:
    def __init__(self, repo: UsuarioRepository):
        self._repo = repo

    async def handle(self, query: VerificarTutorMateriaQuery) -> bool:
        tutor = await self._repo.buscar_por_correo(query.tutor_id)
        if not tutor:
            tutor = await self._repo.buscar_por_id(query.tutor_id)
        if not tutor:
            return False
        return tutor.puede_dictar(query.materia_id)


# ──────────────────────────────────────────────
# Query: Obtener nombre
# ──────────────────────────────────────────────
@dataclass
class ObtenerNombreQuery:
    usuario_id: str


class ObtenerNombreHandler:
    def __init__(self, repo: UsuarioRepository):
        self._repo = repo

    async def handle(self, query: ObtenerNombreQuery) -> str:
        usuario = await self._repo.buscar_por_correo(query.usuario_id)
        if not usuario:
            usuario = await self._repo.buscar_por_id(query.usuario_id)
        return usuario.nombre if usuario else query.usuario_id
