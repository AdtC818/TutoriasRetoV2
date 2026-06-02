import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

export default function OAuthCallback() {
  const navigate = useNavigate();

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const token = params.get("token");
    const rol   = params.get("rol");

    if (token) {
      localStorage.setItem("token", token);
      localStorage.setItem("rol", rol || "ESTUDIANTE");

      // Redirigir según rol
      if (rol === "TUTOR") {
        navigate("/tutor");
      } else {
        navigate("/dashboard");
      }
    } else {
      // Si no hay token, algo salió mal
      navigate("/login?error=oauth");
    }
  }, [navigate]);

  return (
    <div style={{
      display: "flex",
      flexDirection: "column",
      alignItems: "center",
      justifyContent: "center",
      minHeight: "100vh",
      fontFamily: "system-ui, sans-serif",
      color: "#374151",
      gap: 16,
    }}>
      <div style={{
        width: 40,
        height: 40,
        border: "3px solid #e5e7eb",
        borderTop: "3px solid #4f46e5",
        borderRadius: "50%",
        animation: "spin 0.8s linear infinite",
      }} />
      <p>Iniciando sesión con Google...</p>
      <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
    </div>
  );
}
