package com.vitalis.eps

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.vitalis.eps.theme.VitalisTheme
import com.vitalis.eps.ui.EstadoAgenda
import com.vitalis.eps.ui.PantallaDetalle
import com.vitalis.eps.ui.PantallaMaestro
import com.vitalis.eps.ui.PantallaPrincipal

/** Las tres vistas de la app. */
sealed interface Destino {
    data object Principal : Destino
    data object Maestro : Destino
    data class Detalle(val citaId: String) : Destino
}

private fun Destino.aTexto() = when (this) {
    Destino.Principal -> "principal"
    Destino.Maestro -> "maestro"
    is Destino.Detalle -> "detalle:$citaId"
}

private fun destinoDesde(texto: String): Destino = when {
    texto == "maestro" -> Destino.Maestro
    texto.startsWith("detalle:") -> Destino.Detalle(texto.removePrefix("detalle:"))
    else -> Destino.Principal
}

/** Ancho a partir del cual la vista Maestro y la de Detalle se muestran lado a lado (tablet, escritorio). */
private val ANCHO_DOS_PANELES = 840.dp

@Composable
fun App(inicio: List<Destino> = listOf(Destino.Principal)) {
    VitalisTheme {
        // Pila de navegación guardada como texto para que sobreviva a la rotación de pantalla.
        var pilaTexto by rememberSaveable { mutableStateOf(inicio.joinToString("|") { it.aTexto() }) }
        val pila = pilaTexto.split("|").map(::destinoDesde)
        val agenda = remember { EstadoAgenda() }

        fun ir(destino: Destino) {
            pilaTexto = (pila + destino).joinToString("|") { it.aTexto() }
        }
        fun volver() {
            if (pila.size > 1) pilaTexto = pila.dropLast(1).joinToString("|") { it.aTexto() }
        }

        BotonAtras(enabled = pila.size > 1, onBack = ::volver)

        BoxWithConstraints(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            val dosPaneles = maxWidth >= ANCHO_DOS_PANELES
            val actual = pila.last()
            BarraDeEstadoClara(clara = actual is Destino.Principal || (actual is Destino.Detalle && !dosPaneles))

            if (dosPaneles) {
                PantallaAncha(pila, agenda, ::ir, alInicio = { pilaTexto = Destino.Principal.aTexto() }, onCambiarDetalle = { id ->
                    val base = pila.filterNot { it is Destino.Detalle }
                    pilaTexto = (base + Destino.Detalle(id)).joinToString("|") { it.aTexto() }
                })
            } else {
                AnimatedContent(
                    targetState = pila,
                    transitionSpec = { transicion(avanza = targetState.size >= initialState.size) },
                    contentKey = { it.last() },
                    label = "navegacion",
                ) { estado ->
                    when (val destino = estado.last()) {
                        Destino.Principal -> PantallaPrincipal(agenda.proxima, onVerCitas = { ir(Destino.Maestro) })
                        Destino.Maestro -> PantallaMaestro(
                            estado = agenda,
                            seleccionadaId = null,
                            onVolver = ::volver,
                            onSeleccionar = { ir(Destino.Detalle(it.id)) },
                        )
                        is Destino.Detalle -> {
                            val cita = agenda.cita(destino.citaId) ?: agenda.citas.first()
                            PantallaDetalle(cita, onVolver = ::volver, onCambiarEstado = { agenda.cambiarEstado(cita.id, it) })
                        }
                    }
                }
            }
        }
    }
}

/**
 * Al avanzar, la nueva vista entra por la derecha; al volver, sale por la misma derecha.
 * Entrar y salir por el mismo camino ayuda a entender dónde está uno dentro de la app.
 */
private fun transicion(avanza: Boolean): ContentTransform {
    val movimiento = spring(dampingRatio = 1f, stiffness = 380f, visibilityThreshold = IntOffset(1, 1))
    val desvanecer = spring<Float>(dampingRatio = 1f, stiffness = 380f)
    return if (avanza) {
        (slideInHorizontally(movimiento) { it } + fadeIn(desvanecer)) togetherWith
            (slideOutHorizontally(movimiento) { -it / 4 } + fadeOut(desvanecer))
    } else {
        (slideInHorizontally(movimiento) { -it / 4 } + fadeIn(desvanecer)) togetherWith
            (slideOutHorizontally(movimiento) { it } + fadeOut(desvanecer))
    }
}

/** En pantallas anchas: la lista (Maestro) a la izquierda y el Detalle a la derecha. */
@Composable
private fun PantallaAncha(
    pila: List<Destino>,
    agenda: EstadoAgenda,
    ir: (Destino) -> Unit,
    alInicio: () -> Unit,
    onCambiarDetalle: (String) -> Unit,
) {
    val actual = pila.last()
    if (actual is Destino.Principal) {
        PantallaPrincipal(agenda.proxima, onVerCitas = { ir(Destino.Maestro) })
        return
    }
    val seleccionado = (actual as? Destino.Detalle)?.citaId
    Row(Modifier.fillMaxSize()) {
        PantallaMaestro(
            estado = agenda,
            seleccionadaId = seleccionado,
            onVolver = alInicio,
            onSeleccionar = { onCambiarDetalle(it.id) },
            modifier = Modifier.width(420.dp).fillMaxHeight(),
        )
        Box(Modifier.width(1.dp).fillMaxHeight().background(MaterialTheme.colorScheme.outlineVariant))
        Box(Modifier.weight(1f).fillMaxHeight()) {
            AnimatedContent(
                targetState = seleccionado,
                transitionSpec = {
                    fadeIn(spring(dampingRatio = 1f, stiffness = 380f)) togetherWith fadeOut(spring(dampingRatio = 1f, stiffness = 380f))
                },
                label = "detalle",
            ) { id ->
                val cita = id?.let(agenda::cita)
                if (cita == null) {
                    SinSeleccion()
                } else {
                    PantallaDetalle(cita, onVolver = null, onCambiarEstado = { agenda.cambiarEstado(cita.id, it) })
                }
            }
        }
    }
}

@Composable
private fun SinSeleccion() {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
    ) {
        Icon(Icons.Rounded.TouchApp, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(44.dp))
        Text("Elige una cita de la lista", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 12.dp))
        Text(
            "Aquí verás el lugar, la preparación y la autorización.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
