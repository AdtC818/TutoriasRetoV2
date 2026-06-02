"""
Adapter de entrada — OAuth2 con Google (Authlib).
Cumple el requisito de autenticación multifactor de terceros.

Flujo:
  GET /auth/google/login  → redirige a Google
  GET /auth/google/callback → Google regresa con code →
      buscamos o creamos el usuario → devolvemos JWT igual que /auth/login
"""
import os
from fastapi import APIRouter, Request, HTTPException
from fastapi.responses import RedirectResponse
from authlib.integrations.starlette_client import OAuth, OAuthError

from app.auth.security import crear_token_acceso
from app.infrastructure.adapters.mongo_usuario_repository import MongoUsuarioRepository
from app.domain.usuario import Usuario, RolUsuario
from app.database import usuarios_collection

router = APIRouter(prefix="/auth", tags=["OAuth2 Google"])

# ── Configuración OAuth ───────────────────────────────────────────────────────
oauth = OAuth()
oauth.register(
    name="google",
    client_id=os.getenv("GOOGLE_CLIENT_ID", ""),
    client_secret=os.getenv("GOOGLE_CLIENT_SECRET", ""),
    server_metadata_url="https://accounts.google.com/.well-known/openid-configuration",
    client_kwargs={"scope": "openid email profile"},
)

FRONTEND_URL = os.getenv("FRONTEND_URL", "http://localhost:5173")


# ── Endpoints ────────────────────────────────────────────────────────────────
@router.get("/google/login")
async def google_login(request: Request):
    """Redirige al usuario a la pantalla de consentimiento de Google."""
    redirect_uri = os.getenv(
        "GOOGLE_REDIRECT_URI",
        "http://localhost:8000/auth/google/callback"
    )
    return await oauth.google.authorize_redirect(request, redirect_uri)


@router.get("/google/callback")
async def google_callback(request: Request):
    """
    Google llama aquí con el code.
    1. Intercambiamos code por token.
    2. Obtenemos el perfil del usuario.
    3. Si no existe en Mongo, lo creamos con rol ESTUDIANTE.
    4. Generamos JWT y redirigimos al frontend.
    """
    try:
        token = await oauth.google.authorize_access_token(request)
    except OAuthError as e:
        raise HTTPException(status_code=400, detail=f"OAuth error: {e.error}")

    user_info = token.get("userinfo")
    if not user_info:
        raise HTTPException(status_code=400, detail="No se pudo obtener info del usuario")

    repo = MongoUsuarioRepository(usuarios_collection)

    # Buscar por OAuth sub primero, luego por correo
    usuario = await repo.buscar_por_oauth("google", user_info["sub"])
    if not usuario:
        usuario = await repo.buscar_por_correo(user_info["email"])

    if not usuario:
        # Crear usuario nuevo (sin contraseña — solo OAuth)
        nuevo = Usuario(
            nombre=user_info.get("name", user_info["email"]),
            correo=user_info["email"],
            password_hash="",          # no aplica para OAuth
            rol=RolUsuario.ESTUDIANTE,
            oauth_provider="google",
            oauth_sub=user_info["sub"],
        )
        await repo.guardar_oauth_usuario(nuevo)
        usuario = await repo.buscar_por_correo(user_info["email"])

    # Generar JWT igual que el login normal
    jwt_token = crear_token_acceso({"sub": usuario.correo, "rol": usuario.rol.value})

    # Redirigir al frontend con el token en query param
    # El frontend lo guarda en localStorage y redirige al dashboard
    return RedirectResponse(
        url=f"{FRONTEND_URL}/oauth-callback?token={jwt_token}&rol={usuario.rol.value}"
    )
