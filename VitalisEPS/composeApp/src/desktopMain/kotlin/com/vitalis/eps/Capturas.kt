package com.vitalis.eps

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
)

private val detalle = Destino.Detalle("4")

private val capturas = listOf(
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
        val escena = ImageComposeScene(ancho, alto, Density(c.densidad)) { App(inicio = c.pila, oscuroInicial = c.oscuro) }
        var tiempo = 0L
        fun cuadros(n: Int) = repeat(n) { escena.render(tiempo); tiempo += 16_000_000L }
        cuadros(30)
        // Da tiempo a que la app lea las citas de la API de XAMPP (si está encendida).
        repeat(20) { Thread.sleep(100); cuadros(2) }
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
        val imagen = escena.render(tiempo)
        val png = imagen.encodeToData(EncodedImageFormat.PNG) ?: error("No se pudo codificar ${c.archivo}")
        File(carpeta, c.archivo).writeBytes(png.bytes)
        escena.close()
        println("✓ ${c.archivo} (${ancho}×${alto})")
    }
}
