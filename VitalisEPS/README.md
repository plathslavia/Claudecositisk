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
