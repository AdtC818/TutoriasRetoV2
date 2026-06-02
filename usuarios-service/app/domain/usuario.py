"""
Domain layer — núcleo DDD del usuarios-service.
Contiene la entidad raíz Usuario, el value object RolUsuario
y el puerto (interfaz) del repositorio.
Ningún archivo de esta capa importa frameworks (FastAPI, Motor, etc.).
"""
from __future__ import annotations
from dataclasses import dataclass, field
from enum import Enum
from typing import Optional, List


# ──────────────────────────────────────────────
# Value Object
# ──────────────────────────────────────────────
class RolUsuario(str, Enum):
    ESTUDIANTE = "ESTUDIANTE"
    TUTOR = "TUTOR"
    ADMINISTRADOR = "ADMINISTRADOR"


# ──────────────────────────────────────────────
# Entidad raíz
# ──────────────────────────────────────────────
@dataclass
class Usuario:
    nombre: str
    correo: str
    password_hash: str
    rol: RolUsuario
    carrera: str = "Ingeniería de Sistemas"
    semestre: int = 5
    materias: List[str] = field(default_factory=list)
    id: Optional[str] = None          # ObjectId de Mongo como str
    oauth_provider: Optional[str] = None   # "google" | None
    oauth_sub: Optional[str] = None        # ID único en el proveedor

    # ── invariantes del dominio ──────────────────
    def __post_init__(self):
        if not self.nombre.strip():
            raise ValueError("El nombre no puede estar vacío")
        if "@" not in self.correo:
            raise ValueError("Correo inválido")

    def puede_dictar(self, materia_id: str) -> bool:
        """Regla de dominio: solo tutores con la materia asignada."""
        return self.rol == RolUsuario.TUTOR and materia_id in self.materias

    def actualizar_materias(self, nuevas: List[str]) -> None:
        """Regla de dominio: solo un TUTOR puede tener materias."""
        if self.rol != RolUsuario.TUTOR:
            raise PermissionError("Solo un TUTOR puede gestionar materias")
        self.materias = nuevas


# ──────────────────────────────────────────────
# Puerto de repositorio (interfaz — sin implementación)
# ──────────────────────────────────────────────
class UsuarioRepository:
    """Puerto de salida.  La capa de infraestructura lo implementa."""

    async def guardar(self, usuario: Usuario) -> str:
        raise NotImplementedError

    async def buscar_por_correo(self, correo: str) -> Optional[Usuario]:
        raise NotImplementedError

    async def buscar_por_id(self, id: str) -> Optional[Usuario]:
        raise NotImplementedError

    async def buscar_por_oauth(self, provider: str, sub: str) -> Optional[Usuario]:
        raise NotImplementedError

    async def actualizar_materias(self, correo: str, materias: List[str]) -> bool:
        raise NotImplementedError
