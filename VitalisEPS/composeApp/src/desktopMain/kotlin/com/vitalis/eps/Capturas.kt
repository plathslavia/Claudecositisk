package com.vitalis.eps

import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.unit.Density
import org.jetbrains.skia.EncodedImageFormat
import java.io.File

/**
 * Renderiza las vistas reales de la app a imágenes PNG, sin emulador.
 * Se ejecuta con: ./gradlew capturas
 */
private class Captura(
    val archivo: String,
    val pila: List<Destino>,
    val anchoDp: Int = 412,
    val altoDp: Int = 892,
    val densidad: Float = 2.625f,
    /** Cuánto desplazar hacia abajo (en "clics" de rueda del mouse) antes de capturar. */
    val desplazamiento: Float = 0f,
    val oscuro: Boolean = false,
    /** Dónde (fracción del ancho) se hace el desplazamiento; en pantallas de dos paneles elige cuál se mueve. */
    val xDesplazamiento: Float = 0.5f,
    /** Cuenta con la que se inicia sesión antes de capturar (nula = sin sesión). */
    val cuenta: Pair<String, String>? = CAMILO,
)

private val CAMILO = "1020456789" to "camilo123"
private val ADMIN = "admin" to "admin123"

private val detalle = Destino.Detalle("4")

private val capturas = listOf(
    // El login va primero: sin ventana, la respuesta de la API llega en un hilo de red y solo la
    // primera escena del proceso la recoge a tiempo para la píldora "Servidor conectado".
    Captura("12_login.png", listOf(Destino.Principal, Destino.Login), cuenta = null),
    Captura("01_principal.png", listOf(Destino.Principal)),
    Captura("02_principal_empresa.png", listOf(Destino.Principal), desplazamiento = 12f),
    Captura("03_principal_contacto.png", listOf(Destino.Principal), desplazamiento = 200f),
    Captura("04_maestro.png", listOf(Destino.Principal, Destino.Maestro)),
    Captura("05_detalle.png", listOf(Destino.Principal, Destino.Maestro, detalle)),
    Captura("06_detalle_info.png", listOf(Destino.Principal, Destino.Maestro, detalle), desplazamiento = 200f),
    Captura(
        "07_tablet_maestro_detalle.png",
        listOf(Destino.Principal, Destino.Maestro, Destino.Detalle("3")),
        anchoDp = 1280, altoDp = 800, densidad = 1.5f,
    ),
    Captura("08_oscuro_principal.png", listOf(Destino.Principal), oscuro = true),
    Captura("09_oscuro_maestro.png", listOf(Destino.Principal, Destino.Maestro), oscuro = true),
    Captura("10_oscuro_detalle.png", listOf(Destino.Principal, Destino.Maestro, Destino.Detalle("3")), oscuro = true),
    Captura("13_admin.png", listOf(Destino.Principal, Destino.Admin), cuenta = ADMIN),
    Captura("14_admin_nueva_cita.png", listOf(Destino.Principal, Destino.Admin, Destino.NuevaCita), cuenta = ADMIN),
    Captura("15_principal_sin_sesion.png", listOf(Destino.Principal), cuenta = null),
    Captura(
        "16_tablet_admin.png",
        listOf(Destino.Principal, Destino.Admin, Destino.Detalle("11")),
        anchoDp = 1280, altoDp = 800, densidad = 1.5f, cuenta = ADMIN,
    ),
    Captura(
        "11_horizontal.png",
        listOf(Destino.Principal, Destino.Maestro, Destino.Detalle("6")),
        anchoDp = 892, altoDp = 412, densidad = 2.625f, desplazamiento = 24f, xDesplazamiento = 0.2f,
    ),
)

@OptIn(ExperimentalComposeUiApi::class)
fun main(args: Array<String>) {
    val carpeta = File(args.firstOrNull() ?: "capturas").apply { mkdirs() }
    for (c in capturas) {
        val ancho = (c.anchoDp * c.densidad).toInt()
        val alto = (c.altoDp * c.densidad).toInt()
        val escena = ImageComposeScene(ancho, alto, Density(c.densidad)) { App(inicio = c.pila, oscuroInicial = c.oscuro, cuentaInicial = c.cuenta) }
        var tiempo = 0L
        // Cada render devuelve una imagen en memoria nativa: hay que cerrarla o se acumulan gigas.
        fun cuadros(n: Int, paso: Long = 16_000_000L) = repeat(n) {
            // Aplica los cambios de estado que llegaron desde el hilo de red (en Android y en la ventana
            // de escritorio esto lo hace solo el GlobalSnapshotManager; aquí no hay ventana).
            Snapshot.sendApplyNotifications()
            escena.render(tiempo).close()
            tiempo += paso
        }
        cuadros(30)
        // Da tiempo a que la app inicie sesión y lea las citas de la API de XAMPP (si está encendida).
        repeat(50) { Thread.sleep(100); cuadros(2) }
        cuadros(50, paso = 100_000_000L) // deja terminar la animación del pulso (5 s en pasos de 100 ms)
        if (c.desplazamiento > 0f) {
            val punto = Offset(ancho * c.xDesplazamiento, alto / 2f)
            escena.sendPointerEvent(PointerEventType.Move, punto)
            escena.sendPointerEvent(
                PointerEventType.Scroll,
                punto,
                scrollDelta = Offset(0f, c.desplazamiento),
            )
            // El puntero sale de la ventana para que ningún elemento quede resaltado.
            escena.sendPointerEvent(PointerEventType.Exit, Offset(-1f, -1f))
            cuadros(90)
        }
        Snapshot.sendApplyNotifications()
        val imagen = escena.render(tiempo)
        val png = imagen.encodeToData(EncodedImageFormat.PNG) ?: error("No se pudo codificar ${c.archivo}")
        File(carpeta, c.archivo).writeBytes(png.bytes)
        escena.close()
        println("✓ ${c.archivo} (${ancho}×${alto})")
    }
}
