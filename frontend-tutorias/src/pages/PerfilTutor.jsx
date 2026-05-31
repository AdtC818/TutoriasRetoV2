import { useState, useEffect } from "react";

const API_BASE = import.meta.env.VITE_API_URL || "http://localhost:8080";

// Materias disponibles en el catálogo (mock — se pueden conectar al catalogo-service)
const MATERIAS_DISPONIBLES = [
  { id: "MAT001", nombre: "Cálculo Diferencial" },
  { id: "MAT002", nombre: "Cálculo Integral" },
  { id: "MAT003", nombre: "Álgebra Lineal" },
  { id: "PROG001", nombre: "Programación I" },
  { id: "PROG002", nombre: "Programación II" },
  { id: "PROG003", nombre: "Estructuras de Datos" },
  { id: "DB001", nombre: "Bases de Datos" },
  { id: "NET001", nombre: "Redes de Computadores" },
  { id: "SO001", nombre: "Sistemas Operativos" },
  { id: "ING001", nombre: "Ingeniería de Software I" },
  { id: "ING002", nombre: "Ingeniería de Software II" },
  { id: "FIS001", nombre: "Física Mecánica" },
];

export default function PerfilTutor() {
  const [perfil, setPerfil] = useState(null);
  const [materiasSeleccionadas, setMateriasSeleccionadas] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [guardando, setGuardando] = useState(false);
  const [mensaje, setMensaje] = useState(null); // { tipo: "ok"|"error", texto: "" }

  const token = localStorage.getItem("token");

  // ── Cargar perfil al montar ──────────────────────────────────────────────
  useEffect(() => {
    cargarPerfil();
  }, []);

  async function cargarPerfil() {
    setCargando(true);
    try {
      const res = await fetch(`${API_BASE}/auth/me`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      if (!res.ok) throw new Error("No autorizado");
      const data = await res.json();
      setPerfil(data);
      setMateriasSeleccionadas(data.materias || []);
    } catch (e) {
      setMensaje({ tipo: "error", texto: "No se pudo cargar el perfil." });
    } finally {
      setCargando(false);
    }
  }

  // ── Toggle materia ───────────────────────────────────────────────────────
  function toggleMateria(id) {
    setMateriasSeleccionadas((prev) =>
      prev.includes(id) ? prev.filter((m) => m !== id) : [...prev, id]
    );
  }

  // ── Guardar materias ─────────────────────────────────────────────────────
  async function guardarMaterias() {
    setGuardando(true);
    setMensaje(null);
    try {
      const res = await fetch(`${API_BASE}/auth/actualizar-materias`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ materias: materiasSeleccionadas }),
      });
      if (!res.ok) {
        const err = await res.json();
        throw new Error(err.detail || "Error al guardar");
      }
      setMensaje({ tipo: "ok", texto: "Materias actualizadas correctamente." });
    } catch (e) {
      setMensaje({ tipo: "error", texto: e.message });
    } finally {
      setGuardando(false);
    }
  }

  // ── UI ───────────────────────────────────────────────────────────────────
  if (cargando) {
    return (
      <div style={styles.centrado}>
        <div style={styles.spinner} />
        <p style={styles.textoSecundario}>Cargando perfil...</p>
      </div>
    );
  }

  if (!perfil) {
    return (
      <div style={styles.centrado}>
        <p style={styles.error}>No se pudo cargar el perfil del tutor.</p>
      </div>
    );
  }

  if (perfil.rol !== "TUTOR") {
    return (
      <div style={styles.centrado}>
        <p style={styles.error}>Esta sección es solo para tutores.</p>
      </div>
    );
  }

  const materiasNombres = MATERIAS_DISPONIBLES.filter((m) =>
    materiasSeleccionadas.includes(m.id)
  ).map((m) => m.nombre);

  return (
    <div style={styles.pagina}>
      {/* ── Encabezado ───────────────────────────────────────────────── */}
      <div style={styles.encabezado}>
        <div style={styles.avatar}>{perfil.nombre.charAt(0).toUpperCase()}</div>
        <div>
          <h1 style={styles.titulo}>{perfil.nombre}</h1>
          <p style={styles.textoSecundario}>{perfil.correo}</p>
          <span style={styles.badge}>Tutor</span>
        </div>
      </div>

      {/* ── Info personal ────────────────────────────────────────────── */}
      <div style={styles.tarjeta}>
        <h2 style={styles.subtitulo}>Información personal</h2>
        <div style={styles.grid2}>
          <Campo label="Carrera" valor={perfil.carrera} />
          <Campo label="Semestre" valor={perfil.semestre} />
        </div>
      </div>

      {/* ── Materias activas ─────────────────────────────────────────── */}
      <div style={styles.tarjeta}>
        <h2 style={styles.subtitulo}>Materias que dictas actualmente</h2>
        {materiasNombres.length === 0 ? (
          <p style={styles.textoSecundario}>
            Aún no tienes materias seleccionadas.
          </p>
        ) : (
          <div style={styles.chips}>
            {materiasNombres.map((n) => (
              <span key={n} style={styles.chip}>
                {n}
              </span>
            ))}
          </div>
        )}
      </div>

      {/* ── Selector de materias ──────────────────────────────────────── */}
      <div style={styles.tarjeta}>
        <h2 style={styles.subtitulo}>Gestionar materias</h2>
        <p style={styles.textoSecundario} style={{ marginBottom: 16 }}>
          Selecciona las materias que puedes dictar como tutor.
        </p>

        <div style={styles.gridMaterias}>
          {MATERIAS_DISPONIBLES.map((m) => {
            const activa = materiasSeleccionadas.includes(m.id);
            return (
              <button
                key={m.id}
                onClick={() => toggleMateria(m.id)}
                style={{
                  ...styles.btnMateria,
                  ...(activa ? styles.btnMateriaActiva : {}),
                }}
              >
                <span style={styles.checkIcon}>{activa ? "✓" : "+"}</span>
                {m.nombre}
              </button>
            );
          })}
        </div>

        {/* Mensaje de feedback */}
        {mensaje && (
          <div
            style={{
              ...styles.mensajeBox,
              background: mensaje.tipo === "ok" ? "#d1fae5" : "#fee2e2",
              color: mensaje.tipo === "ok" ? "#065f46" : "#991b1b",
            }}
          >
            {mensaje.texto}
          </div>
        )}

        <button
          onClick={guardarMaterias}
          disabled={guardando}
          style={{
            ...styles.btnGuardar,
            opacity: guardando ? 0.6 : 1,
          }}
        >
          {guardando ? "Guardando..." : "Guardar materias"}
        </button>
      </div>
    </div>
  );
}

// ── Sub-componente ───────────────────────────────────────────────────────────
function Campo({ label, valor }) {
  return (
    <div>
      <p style={{ margin: 0, fontSize: 12, color: "#6b7280" }}>{label}</p>
      <p style={{ margin: "2px 0 0", fontWeight: 500 }}>{valor}</p>
    </div>
  );
}

// ── Estilos ──────────────────────────────────────────────────────────────────
const styles = {
  pagina: {
    maxWidth: 720,
    margin: "0 auto",
    padding: "32px 16px",
    fontFamily: "system-ui, sans-serif",
    color: "#111827",
  },
  encabezado: {
    display: "flex",
    alignItems: "center",
    gap: 20,
    marginBottom: 28,
  },
  avatar: {
    width: 64,
    height: 64,
    borderRadius: "50%",
    background: "#4f46e5",
    color: "#fff",
    fontSize: 28,
    fontWeight: 600,
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    flexShrink: 0,
  },
  titulo: { margin: 0, fontSize: 22, fontWeight: 600 },
  textoSecundario: { margin: "4px 0 0", fontSize: 14, color: "#6b7280" },
  badge: {
    display: "inline-block",
    marginTop: 6,
    padding: "2px 10px",
    borderRadius: 12,
    background: "#ede9fe",
    color: "#5b21b6",
    fontSize: 12,
    fontWeight: 500,
  },
  tarjeta: {
    background: "#fff",
    border: "1px solid #e5e7eb",
    borderRadius: 12,
    padding: 24,
    marginBottom: 20,
  },
  subtitulo: { margin: "0 0 16px", fontSize: 16, fontWeight: 500 },
  grid2: {
    display: "grid",
    gridTemplateColumns: "1fr 1fr",
    gap: 16,
  },
  chips: { display: "flex", flexWrap: "wrap", gap: 8 },
  chip: {
    padding: "4px 12px",
    borderRadius: 20,
    background: "#ede9fe",
    color: "#5b21b6",
    fontSize: 13,
  },
  gridMaterias: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fill, minmax(200px, 1fr))",
    gap: 10,
    marginBottom: 20,
  },
  btnMateria: {
    display: "flex",
    alignItems: "center",
    gap: 8,
    padding: "10px 14px",
    border: "1px solid #e5e7eb",
    borderRadius: 8,
    background: "#f9fafb",
    color: "#374151",
    fontSize: 13,
    cursor: "pointer",
    textAlign: "left",
    transition: "all 0.15s",
  },
  btnMateriaActiva: {
    background: "#ede9fe",
    border: "1px solid #8b5cf6",
    color: "#5b21b6",
  },
  checkIcon: { fontSize: 14, fontWeight: 700, width: 16, textAlign: "center" },
  mensajeBox: {
    padding: "10px 14px",
    borderRadius: 8,
    marginBottom: 16,
    fontSize: 14,
  },
  btnGuardar: {
    padding: "10px 24px",
    background: "#4f46e5",
    color: "#fff",
    border: "none",
    borderRadius: 8,
    fontSize: 14,
    fontWeight: 500,
    cursor: "pointer",
  },
  centrado: {
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
    minHeight: 200,
    gap: 12,
  },
  spinner: {
    width: 32,
    height: 32,
    border: "3px solid #e5e7eb",
    borderTop: "3px solid #4f46e5",
    borderRadius: "50%",
    animation: "spin 0.8s linear infinite",
  },
  error: { color: "#991b1b", fontSize: 15 },
};
