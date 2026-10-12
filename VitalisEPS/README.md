# Vitalis EPS — Guía 7: Vistas Maestro–Detalle

Aplicación multiplataforma (Android y escritorio) hecha con **Kotlin Multiplatform + Compose Multiplatform**.
App para que los afiliados de una EPS ficticia gestionen sus citas médicas.

Integrantes: Andrés García · Camilo Casallas · Cristóbal Moncada

| Vista | Archivo | Qué muestra |
|---|---|---|
| Principal (Main) | `ui/PantallaPrincipal.kt` | Portada de la EPS con la próxima cita, cifras, quiénes somos, misión, visión, servicios y canales de atención |
| Maestro | `ui/PantallaMaestro.kt` | "Mis citas": resumen por estado, búsqueda, filtros y lista agrupada por mes |
| Detalle | `ui/PantallaDetalle.kt` | La cita elegida: profesional, fecha y hora, lugar, preparación, autorización; confirmar o cancelar |
| Inicio de sesión | `ui/PantallaLogin.kt` | Documento y contraseña; cuentas de prueba con un toque |
| Panel de administración | `ui/PantallaAdmin.kt`, `ui/Formularios.kt` | Todas las citas y los profesionales; crear, confirmar, marcar atendida, cancelar o eliminar |

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

## Iniciar sesión y panel de administración

La portada (vista Principal) es pública. Para ver citas hay que **iniciar sesión**:

| Rol | Usuario | Contraseña | Qué puede hacer |
|---|---|---|---|
| Afiliado | `1020456789` (Camilo Casallas) | `camilo123` | Ver **sus** citas, confirmarlas o cancelarlas |
| Afiliado | `1018223344` (Andrés García) | `andres123` | Igual, con sus propias citas |
| Afiliado | `1032667788` (Cristóbal Moncada) | `cristobal123` | Igual, con sus propias citas |
| Administrador | `admin` | `admin123` | Panel de administración: ver todas las citas, crear citas y profesionales, confirmar, marcar como atendida, cancelar o eliminar |

En la pantalla de login hay botones que llenan estas cuentas con un toque.
Si XAMPP no está encendido, las mismas cuentas funcionan en **modo demostración** con datos de ejemplo
(los cambios se pierden al cerrar la app).

**Seguridad en el servidor:**
- Las contraseñas se guardan cifradas con `password_hash` (bcrypt); nunca en texto plano.
- Al iniciar sesión se crea un token aleatorio que vence en 8 horas; la app lo envía en la cabecera `X-Token`.
- Cada archivo PHP revisa el rol: un afiliado solo recibe y modifica sus propias citas, y no puede usar
  las funciones de administración (responde 403).
- Todas las consultas usan sentencias preparadas (PDO), así que la inyección SQL no funciona.
- El mismo mensaje para "usuario no existe" y "contraseña incorrecta", para no revelar qué usuarios existen.

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
| `servidor/vitalis_eps.sql` | Crea las tablas `afiliados`, `especialidades`, `profesionales`, `sedes`, `citas`, `preparaciones`, `usuarios` y `sesiones`, con datos y cuentas de prueba |
| `vitalis_api/login.php` | `POST {"usuario", "clave"}` → abre la sesión y devuelve el token y el rol |
| `vitalis_api/logout.php` | `POST` → cierra la sesión |
| `vitalis_api/citas.php` | `GET` → citas con su preparación (afiliado: las suyas; admin: todas) |
| `vitalis_api/cambiar_estado.php` | `POST {"id", "estado"}` → confirmar o cancelar (admin: cualquier estado) |
| `vitalis_api/admin_catalogos.php` | `GET` → afiliados, profesionales y sedes para los formularios (solo admin) |
| `vitalis_api/admin_crear_cita.php` | `POST` → crea una cita con sus pasos de preparación, validando cada dato (solo admin) |
| `vitalis_api/admin_crear_profesional.php` | `POST {"nombre", "registro", "especialidad"}` (solo admin) |
| `vitalis_api/admin_eliminar_cita.php` | `POST {"id"}` (solo admin) |
| `vitalis_api/conexion.php` | Conexión a MySQL (usuario `root` sin contraseña, como viene XAMPP) y validación de la sesión |

Todas las peticiones, salvo `login.php` e `index.php`, necesitan la cabecera `X-Token` con el token de la sesión.

> **Si ya habías importado la base de datos antes**, vuelve a importar `vitalis_eps.sql` (crea las tablas
> nuevas de usuarios y sesiones) y vuelve a copiar la carpeta `vitalis_api` completa a `htdocs`.

En la app, el cliente está en `data/Servidor.kt` (Ktor); la sesión, la sincronización y las acciones de
administración, en `ui/EstadoAgenda.kt`; las pantallas nuevas, en `ui/PantallaLogin.kt`, `ui/PantallaAdmin.kt`
y `ui/Formularios.kt`.

## Modo oscuro y estilos

El modo oscuro sigue el tema del celular; el botón sol/luna (en la portada y en *Mis citas*) lo cambia a mano.
El botón de paleta de la portada abre el selector de estilo: **Pulso**, **Calma**, **Coral** y **Neón**
(colores y tipografía en `theme/Estilos.kt`).

## Si la app se siente lenta

1. **Prueba la versión release, no la debug.** Android Studio instala por defecto la versión *debug*, que
   puede ser varias veces más lenta en Compose: es depurable y no usa los perfiles de optimización de
   las librerías. Para probar la rápida: *View → Tool Windows → Build Variants* → en `composeApp`
   elige **release** → Run ▶. Ya viene firmada con la llave de depuración, así que instala sin más pasos.
2. **Emulador:** que use aceleración por hardware (en *Device Manager* → editar → *Graphics: Hardware*).
   Un celular real siempre da una idea más fiel.
3. **Para ver qué se recompone de más:** *Tools → Layout Inspector* → activa *Show Recomposition Counts*.
   Si un número sube sin que toques nada, ese componente está trabajando de más.
4. **Para medir cuadros lentos:** en el celular, *Opciones de desarrollador → Renderización de GPU del
   perfil → En pantalla como barras*. Las barras que pasan la línea verde tardaron más de 16 ms.

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
