# Nodo Central — Módulo de Administración (CU-10)

Backend del **módulo de administración** del Sistema de emergencias sísmicas (CINVESTAV).
Corresponde a la **HU-02 / CU-10** del prototipo (Sprint 2).

Guarda la **configuración maestra** del sistema (edificios, zonas, nodos FOG, usuarios y
parámetros) y la **sincroniza** hacia los nodos FOG de cada edificio. El módulo opera solo
cuando hay conectividad con los nodos locales (si un nodo no responde, queda `OFFLINE` y la
config queda pendiente de aplicar).

> ⚠️ Estos archivos **no se compilaron** en este entorno (sin acceso a Maven Central).
> Revísalos al importarlos. Stack idéntico al Nodo FOG del Sprint 1: Spring Boot 3.3.5 / Java 21 / H2.

---

## 1. Cómo correrlo

```bash
cd nodo-central
mvn spring-boot:run
```

Arranca en **http://localhost:9090**. Al primer arranque siembra datos de ejemplo
(2 edificios, 3 zonas, 1 nodo FOG, 3 usuarios y 3 parámetros).

- Consola H2 para inspeccionar la BD: `http://localhost:9090/h2`
  (JDBC URL `jdbc:h2:file:./data/central`, usuario `sa`, sin contraseña).

---

## 2. Endpoints REST

Todas las **escrituras** (`POST`/`PUT`/`DELETE`) requieren el header `X-Role: ADMINISTRADOR`
(mismo esquema que el Nodo FOG; el JWT real entra en un sprint posterior). Las lecturas (`GET`)
son abiertas en el prototipo.

| Recurso | Métodos |
|---|---|
| `/api/edificios` | GET, POST, PUT/{id}, DELETE/{id} |
| `/api/zonas` (`?edificioId=`) | GET, POST, PUT/{id}, DELETE/{id} |
| `/api/nodos` | GET, POST, PUT/{id}, DELETE/{id} |
| `/api/usuarios` (`?edificioId=`) | GET, POST, PUT/{id}, DELETE/{id} |
| `/api/parametros` | GET, POST, PUT/{id}, DELETE/{id} |
| `/api/sync` | POST (sincroniza todos) |
| `/api/sync/{nodoId}` | POST (sincroniza uno) |

Ejemplos:

```bash
# Crear un edificio
curl -X POST http://localhost:9090/api/edificios \
  -H "Content-Type: application/json" -H "X-Role: ADMINISTRADOR" \
  -d '{"clave":"C","nombre":"Edificio C - Biblioteca","descripcion":""}'

# Crear una zona segura del edificio 1
curl -X POST http://localhost:9090/api/zonas \
  -H "Content-Type: application/json" -H "X-Role: ADMINISTRADOR" \
  -d '{"nombre":"Punto de reunion norte","tipo":"SEGURA","edificioId":1,"piso":0}'

# Sincronizar la config a todos los nodos FOG
curl -X POST http://localhost:9090/api/sync -H "X-Role: ADMINISTRADOR"
```

---

## 3. Contrato de sincronización (lo que el Nodo FOG debe implementar)

El nodo central envía un `POST` a **`{baseUrl-del-nodo}/config/sync`** con este JSON.
El Nodo FOG debe exponer ese endpoint, guardar la config localmente (CA-03) y responder `200`.

```json
{
  "generadoEn": "2026-06-01T10:00:00Z",
  "edificioClave": "A",
  "edificioNombre": "Edificio A - Computacion",
  "zonas": [
    { "id": 1, "nombre": "Punto de reunion patio central", "tipo": "SEGURA", "piso": 0 }
  ],
  "usuarios": [
    { "id": 3, "identificador": "usr-001", "nombre": "Usuaria Ana", "rol": "USUARIO", "activo": true }
  ],
  "parametros": {
    "frecuencia_refresco_seg": "3",
    "timeout_emergencia_min": "60"
  }
}
```

El central espera un `2xx`. Cualquier error o timeout (`central.sync.timeout-ms`, default 2000 ms)
marca el nodo como `OFFLINE`.

---

## 4. Modelo de datos

- **Edificio** — clave única, nombre, descripción
- **Zona** — nombre, tipo (`SEGURA`/`NORMAL`/`RIESGO`), piso, edificio
- **NodoFog** — nombre, host, puerto, edificio, estado (`ONLINE`/`OFFLINE`/`DESCONOCIDO`), última sincronización
- **Usuario** — identificador único, nombre, rol, edificio de registro, activo
  *(los datos médicos NO viven aquí: van cifrados en el nodo FOG, por R7/R8)*
- **Parametro** — clave/valor + descripción

---

## 5. Lo que queda para los siguientes pasos

- [ ] **Frontend web del admin** (consume este REST) — siguiente entregable del Sprint 2.
- [ ] **Endpoint `/config/sync` en el Nodo FOG** que reciba y persista la config.
- [ ] **JWT** en lugar del header `X-Role` (sprint de seguridad, junto con el Nodo FOG).
- [ ] **HTTPS/TLS** para la sincronización entre central y nodos (mismo sprint de seguridad).
