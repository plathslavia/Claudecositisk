# Vitalis EPS — Guía 7: Vistas Maestro–Detalle

Aplicación multiplataforma (Android y escritorio) hecha con **Kotlin Multiplatform + Compose Multiplatform**.
App para que los afiliados de una EPS ficticia gestionen sus citas médicas.

Integrantes: Andrés García · Camilo Casallas · Cristóbal Moncada

| Vista | Archivo | Qué muestra |
|---|---|---|
| Principal (Main) | `ui/PantallaPrincipal.kt` | Portada de la EPS con la próxima cita, cifras, quiénes somos, misión, visión, servicios y canales de atención |
| Maestro | `ui/PantallaMaestro.kt` | "Mis citas": resumen por estado, búsqueda, filtros y lista agrupada por mes |
| Detalle | `ui/PantallaDetalle.kt` | La cita elegida: profesional, fecha y hora, lugar, preparación, autorización; confirmar o cancelar |

La navegación está en `App.kt`. En pantallas de 840 dp o más (tablet o escritorio), el Maestro y el Detalle se muestran lado a lado.

## Abrir y ejecutar en Android Studio

1. **File → Open** y elegir la carpeta `VitalisEPS` (no la raíz del repositorio).
2. Esperar a que termine la sincronización de Gradle. La primera vez descarga las dependencias.
3. Arriba, en la lista de configuraciones, elegir **composeApp** y un emulador (o un celular con depuración USB).
4. Pulsar **Run ▶**.

Requisitos: Android Studio Koala (2024.1) o más reciente, con el **SDK de Android 35** instalado.
Si Android Studio avisa que falta, pulsar "Install missing SDK" o instalarlo en *Tools → SDK Manager*.

Si la sincronización falla con un error de versión de Java, ir a
*Settings → Build, Execution, Deployment → Build Tools → Gradle → Gradle JDK* y elegir **JDK 17** o **21**.

## Conectar con la base de datos (XAMPP)

La app lee y guarda las citas en MySQL a través de una pequeña API en PHP. Si el servidor no responde,
muestra datos de ejemplo y lo avisa con la píldora "Sin servidor · datos de ejemplo" en *Mis citas*.

1. Abre el **XAMPP Control Panel** y pulsa **Start** en **Apache** y en **MySQL**.
2. Entra a <http://localhost/phpmyadmin> → pestaña **Importar** → elige `servidor/vitalis_eps.sql` → **Continuar**.
   Se crea la base `vitalis_eps` con sus tablas y datos.
3. Copia la carpeta `servidor/vitalis_api` dentro de `C:\xampp\htdocs\` (queda `C:\xampp\htdocs\vitalis_api`).
4. Prueba en el navegador: <http://localhost/vitalis_api/> debe responder `"ok": true` y el número de citas.
5. Ejecuta la app:
   - **Emulador de Android:** funciona tal cual; `10.0.2.2` es el `localhost` del computador.
   - **Celular real:** conéctalo a la misma red Wi-Fi que el PC, busca la IP del PC (`ipconfig` en Windows),
     toca la píldora de conexión en *Mis citas* y escribe `http://TU-IP/vitalis_api`. Si no conecta, permite
     Apache en el Firewall de Windows.
   - **Escritorio:** usa `http://localhost/vitalis_api`.

Si tu MySQL tiene contraseña, cámbiala en `vitalis_api/conexion.php` (`DB_CLAVE`).

| Archivo | Qué hace |
|---|---|
| `servidor/vitalis_eps.sql` | Crea las tablas `afiliados`, `especialidades`, `profesionales`, `sedes`, `citas` y `preparaciones` con datos de ejemplo |
| `vitalis_api/citas.php` | `GET ?afiliado=1` → citas del afiliado con sus pasos de preparación, en JSON |
| `vitalis_api/cambiar_estado.php` | `POST {"id": 4, "estado": "Confirmada"}` → guarda la confirmación o cancelación |
| `vitalis_api/conexion.php` | Datos de conexión a MySQL (usuario `root` sin contraseña, como viene XAMPP) |

En la app, el cliente está en `data/Servidor.kt` (Ktor) y la lógica de sincronización en `ui/EstadoAgenda.kt`.

## Modo oscuro

Sigue el tema del celular. El botón sol/luna (en la portada y en *Mis citas*) lo cambia manualmente.

## Ejecutar en escritorio

```bash
./gradlew :composeApp:run
```

## Generar las capturas de pantalla

```bash
./gradlew capturas
```

Renderiza las vistas reales a PNG en `capturas/`, sin emulador. Después, desde la raíz del repositorio:

```bash
pip install python-docx
python3 entrega/generar_guia.py
```

Ese script inserta las capturas en la guía (`entrega/Guia_7_Master_Detail_Vitalis_EPS.docx`).
Si prefieres capturas tomadas del emulador, guárdalas en `capturas/` con los mismos nombres y vuelve a ejecutar el script.
