"""
Infrastructure layer — Adaptador de salida.

MongoUsuarioRepository implementa el puerto UsuarioRepository
usando Motor (MongoDB asíncrono). Es el único archivo que conoce
la colección de Mongo — el dominio jamás la toca directamente.
"""
from __future__ import annotations
from typing import Optional, List

from bson import ObjectId
from bson.errors import InvalidId

from app.domain.usuario import Usuario, RolUsuario, UsuarioRepository


class MongoUsuarioRepository(UsuarioRepository):
    """Adaptador de salida: MongoDB a través de Motor."""

    def __init__(self, collection):
        self._col = collection

    # ── helpers ──────────────────────────────────────
    @staticmethod
    def _doc_to_entity(doc: dict) -> Usuario:
        return Usuario(
            id=str(doc["_id"]),
            nombre=doc["nombre"],
            correo=doc["correo"],
            password_hash=doc.get("password_hash", ""),
            rol=RolUsuario(doc.get("rol", "ESTUDIANTE")),
            carrera=doc.get("carrera", "Ingeniería de Sistemas"),
            semestre=doc.get("semestre", 5),
            materias=doc.get("materias", []),
            oauth_provider=doc.get("oauth_provider"),
            oauth_sub=doc.get("oauth_sub"),
        )

    @staticmethod
    def _entity_to_doc(usuario: Usuario) -> dict:
        doc = {
            "nombre": usuario.nombre,
            "correo": usuario.correo,
            "password_hash": usuario.password_hash,
            "rol": usuario.rol.value,
            "carrera": usuario.carrera,
            "semestre": usuario.semestre,
            "materias": usuario.materias,
        }
        if usuario.oauth_provider:
            doc["oauth_provider"] = usuario.oauth_provider
        if usuario.oauth_sub:
            doc["oauth_sub"] = usuario.oauth_sub
        return doc

    # ── puerto ───────────────────────────────────────
    async def guardar(self, usuario: Usuario) -> str:
        doc = self._entity_to_doc(usuario)
        resultado = await self._col.insert_one(doc)
        return str(resultado.inserted_id)

    async def buscar_por_correo(self, correo: str) -> Optional[Usuario]:
        doc = await self._col.find_one({"correo": correo})
        return self._doc_to_entity(doc) if doc else None

    async def buscar_por_id(self, id: str) -> Optional[Usuario]:
        try:
            doc = await self._col.find_one({"_id": ObjectId(id)})
            return self._doc_to_entity(doc) if doc else None
        except InvalidId:
            return None

    async def buscar_por_oauth(self, provider: str, sub: str) -> Optional[Usuario]:
        doc = await self._col.find_one({"oauth_provider": provider, "oauth_sub": sub})
        return self._doc_to_entity(doc) if doc else None

    async def actualizar_materias(self, correo: str, materias: List[str]) -> bool:
        r = await self._col.update_one(
            {"correo": correo},
            {"$set": {"materias": materias}}
        )
        return r.matched_count > 0

    async def guardar_oauth_usuario(self, usuario: Usuario) -> Usuario:
        """Inserta o actualiza un usuario que viene de OAuth2."""
        doc = self._entity_to_doc(usuario)
        result = await self._col.find_one_and_update(
            {"correo": usuario.correo},
            {"$set": doc},
            upsert=True,
            return_document=True,
        )
        return self._doc_to_entity(result)
