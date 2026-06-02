"""
Application layer — Commands (escritura / lado Write del CQRS).

RegistrarUsuarioCommand  → crea un usuario nuevo
ActualizarMateriasCommand → actualiza la lista de materias de un tutor
"""
from __future__ import annotations
from dataclasses import dataclass, field
from typing import List

from app.domain.usuario import Usuario, RolUsuario, UsuarioRepository
from app.auth.security import obtener_hash_password


# ──────────────────────────────────────────────
# Command: Registrar usuario
# ──────────────────────────────────────────────
@dataclass
class RegistrarUsuarioCommand:
    nombre: str
    correo: str
    password: str
    rol: str
    carrera: str = "Ingeniería de Sistemas"
    semestre: int = 5
    materias: List[str] = field(default_factory=list)


class RegistrarUsuarioHandler:
    def __init__(self, repo: UsuarioRepository):
        self._repo = repo

    async def handle(self, cmd: RegistrarUsuarioCommand) -> str:
        # 1. Verificar unicidad (regla de negocio)
        existente = await self._repo.buscar_por_correo(cmd.correo)
        if existente:
            raise ValueError("El correo ya está registrado")

        # 2. Construir entidad de dominio
        usuario = Usuario(
            nombre=cmd.nombre,
            correo=cmd.correo,
            password_hash=obtener_hash_password(cmd.password),
            rol=RolUsuario(cmd.rol),
            carrera=cmd.carrera,
            semestre=cmd.semestre,
            materias=cmd.materias,
        )

        # 3. Persistir a través del puerto
        return await self._repo.guardar(usuario)


# ──────────────────────────────────────────────
# Command: Actualizar materias de un tutor
# ──────────────────────────────────────────────
@dataclass
class ActualizarMateriasCommand:
    correo: str
    materias: List[str]


class ActualizarMateriasHandler:
    def __init__(self, repo: UsuarioRepository):
        self._repo = repo

    async def handle(self, cmd: ActualizarMateriasCommand) -> List[str]:
        usuario = await self._repo.buscar_por_correo(cmd.correo)
        if not usuario:
            raise LookupError("Usuario no encontrado")

        # Regla de dominio encapsulada en la entidad
        usuario.actualizar_materias(cmd.materias)

        await self._repo.actualizar_materias(cmd.correo, cmd.materias)
        return cmd.materias
