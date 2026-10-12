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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.vitalis.eps.data.Estado
import com.vitalis.eps.data.Rol
import com.vitalis.eps.theme.Estilo
import com.vitalis.eps.theme.VitalisTheme
import com.vitalis.eps.ui.DialogoEstilos
import com.vitalis.eps.ui.EstadoAgenda
import com.vitalis.eps.ui.PantallaAdmin
import com.vitalis.eps.ui.PantallaDetalle
import com.vitalis.eps.ui.PantallaLogin
import com.vitalis.eps.ui.PantallaMaestro
import com.vitalis.eps.ui.PantallaNuevaCita
import com.vitalis.eps.ui.PantallaPrincipal

/** Las pantallas de la app. Principal, Maestro y Detalle son las tres vistas que pide la guía. */
sealed interface Destino {
    data object Principal : Destino
    data object Login : Destino
    data object Maestro : Destino
    data object Admin : Destino
    data object NuevaCita : Destino
    data class Detalle(val citaId: String) : Destino
}

private fun Destino.aTexto() = when (this) {
    Destino.Principal -> "principal"
    Destino.Login -> "login"
    Destino.Maestro -> "maestro"
    Destino.Admin -> "admin"
    Destino.NuevaCita -> "nueva"
    is Destino.Detalle -> "detalle:$citaId"
}

private fun destinoDesde(texto: String): Destino = when {
    texto == "login" -> Destino.Login
    texto == "maestro" -> Destino.Maestro
    texto == "admin" -> Destino.Admin
    texto == "nueva" -> Destino.NuevaCita
    texto.startsWith("detalle:") -> Destino.Detalle(texto.removePrefix("detalle:"))
    else -> Destino.Principal
}

/** Ancho a partir del cual la lista y el Detalle se muestran lado a lado (tablet, escritorio). */
private val ANCHO_DOS_PANELES = 840.dp

/**
 * @param cuentaInicial usuario y contraseña con los que iniciar sesión al abrir (para demostraciones y capturas).
 */
@Composable
fun App(
    inicio: List<Destino> = listOf(Destino.Principal),
    oscuroInicial: Boolean? = null,
    estiloInicial: Estilo = Estilo.Pulso,
    cuentaInicial: Pair<String, String>? = null,
) {
    // Nulo = seguir el tema del sistema; el botón sol/luna lo fija en claro u oscuro.
    var oscuroElegido by rememberSaveable { mutableStateOf(oscuroInicial) }
    val oscuro = oscuroElegido ?: isSystemInDarkTheme()
    var estilo by rememberSaveable { mutableStateOf(estiloInicial) }
    var eligiendoEstilo by remember { mutableStateOf(false) }
    VitalisTheme(oscuro, estilo) {
        // Pila de navegación guardada como texto para que sobreviva a la rotación de pantalla.
        var pilaTexto by rememberSaveable { mutableStateOf(inicio.joinToString("|") { it.aTexto() }) }
        val pila = pilaTexto.split("|").map(::destinoDesde)
        val agenda = remember { EstadoAgenda() }
        val alcance = rememberCoroutineScope()
        var intento by remember { mutableIntStateOf(0) }
        var aviso by remember { mutableStateOf<String?>(null) }

        // Al abrir la app (y cada vez que se prueba otra dirección) se comprueba el servidor de XAMPP.
        LaunchedEffect(agenda.urlServidor, intento) {
            agenda.probarServidor()
            agenda.sincronizar()
        }
        LaunchedEffect(Unit) {
            cuentaInicial?.let { (usuario, clave) -> agenda.iniciarSesion(usuario, clave) }
        }

        fun irA(nueva: List<Destino>) {
            pilaTexto = nueva.joinToString("|") { it.aTexto() }
        }
        fun ir(destino: Destino) = irA(pila + destino)
        fun volver() {
            if (pila.size > 1) irA(pila.dropLast(1))
        }

        val cambiarTema = { oscuroElegido = !oscuro }
        val elegirEstilo = { eligiendoEstilo = true }
        val reintentar: (String) -> Unit = { url ->
            agenda.urlServidor = url
            intento++
        }
        val cambiarEstado = { id: String, estado: Estado -> agenda.cambiarEstado(id, estado, alcance) }
        val cerrarSesion = {
            agenda.cerrarSesion(alcance)
            irA(listOf(Destino.Principal))
        }
        // Desde la portada: sin sesión va al login; con sesión, a su lista (afiliado) o al panel (admin).
        val abrirZonaPrivada = {
            when (agenda.sesion?.rol) {
                null -> ir(Destino.Login)
                Rol.Admin -> ir(Destino.Admin)
                Rol.Afiliado -> ir(Destino.Maestro)
            }
        }
        val alIngresar = {
            irA(listOf(Destino.Principal, if (agenda.sesion?.rol == Rol.Admin) Destino.Admin else Destino.Maestro))
        }

        if (eligiendoEstilo) {
            DialogoEstilos(actual = estilo, onElegir = { estilo = it }, onCerrar = { eligiendoEstilo = false })
        }
        BotonAtras(enabled = pila.size > 1, onBack = ::volver)

        // ---------------------------------------------------------------- piezas de cada pantalla

        @Composable
        fun Principal() = PantallaPrincipal(
            sesion = agenda.sesion,
            proxima = agenda.proxima,
            onAbrir = abrirZonaPrivada,
            onCambiarTema = cambiarTema,
            onElegirEstilo = elegirEstilo,
            onCerrarSesion = cerrarSesion,
        )

        @Composable
        fun Login() = PantallaLogin(agenda, onVolver = ::volver, onIngresar = alIngresar, onReintentar = reintentar)

        @Composable
        fun Maestro(seleccionada: String?, onElegir: (String) -> Unit, modifier: Modifier = Modifier) = PantallaMaestro(
            estado = agenda,
            seleccionadaId = seleccionada,
            onVolver = { irA(listOf(Destino.Principal)) },
            onSeleccionar = { onElegir(it.id) },
            onCambiarTema = cambiarTema,
            onCerrarSesion = cerrarSesion,
            onReintentar = reintentar,
            modifier = modifier,
        )

        @Composable
        fun Admin(seleccionada: String?, onElegir: (String) -> Unit, modifier: Modifier = Modifier) = PantallaAdmin(
            estado = agenda,
            seleccionadaId = seleccionada,
            aviso = aviso,
            onAvisoMostrado = { aviso = null },
            onVolver = { irA(listOf(Destino.Principal)) },
            onAbrirCita = { onElegir(it.id) },
            onNuevaCita = { ir(Destino.NuevaCita) },
            onCambiarEstado = cambiarEstado,
            onCambiarTema = cambiarTema,
            onCerrarSesion = cerrarSesion,
            onReintentar = reintentar,
            modifier = modifier,
        )

        @Composable
        fun Detalle(id: String?, onVolver: (() -> Unit)?) {
            val cita = id?.let(agenda::cita)
            if (cita == null) {
                SinSeleccion(onVolver)
            } else {
                PantallaDetalle(cita, onVolver = onVolver, onCambiarEstado = { cambiarEstado(cita.id, it) })
            }
        }

        @Composable
        fun NuevaCita() = PantallaNuevaCita(agenda, onVolver = ::volver, onCreada = { mensaje ->
            aviso = mensaje
            volver()
        })

        // Las pantallas privadas exigen sesión: si no la hay (p. ej. la app se cerró), se muestra el login.
        fun permitido(destino: Destino): Destino = when (destino) {
            Destino.Maestro, is Destino.Detalle -> if (agenda.sesion == null) Destino.Login else destino
            Destino.Admin, Destino.NuevaCita -> if (agenda.sesion?.rol != Rol.Admin) Destino.Login else destino
            else -> destino
        }

        BoxWithConstraints(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                // Con el celular de lado, deja libre la zona de la cámara y de los botones del sistema.
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)),
        ) {
            val dosPaneles = maxWidth >= ANCHO_DOS_PANELES
            val actual = permitido(pila.last())
            BarraDeEstadoClara(
                clara = oscuro || actual is Destino.Principal || actual is Destino.Login || (actual is Destino.Detalle && !dosPaneles),
            )

            val listaAncha = when {
                !dosPaneles -> null
                actual is Destino.Maestro || actual is Destino.Admin -> actual
                actual is Destino.Detalle -> if (Destino.Admin in pila) Destino.Admin else Destino.Maestro
                else -> null
            }
            if (listaAncha != null) {
                // Pantalla ancha: la lista (Maestro o panel de admin) a la izquierda y el Detalle a la derecha.
                val seleccionada = (actual as? Destino.Detalle)?.citaId
                val elegir: (String) -> Unit = { id -> irA(pila.filterNot { it is Destino.Detalle } + Destino.Detalle(id)) }
                Row(Modifier.fillMaxSize()) {
                    val ancho = Modifier.width(440.dp).fillMaxHeight()
                    if (listaAncha is Destino.Admin) Admin(seleccionada, elegir, ancho) else Maestro(seleccionada, elegir, ancho)
                    Box(Modifier.width(1.dp).fillMaxHeight().background(MaterialTheme.colorScheme.outlineVariant))
                    Box(Modifier.weight(1f).fillMaxHeight()) {
                        AnimatedContent(
                            targetState = seleccionada,
                            transitionSpec = {
                                fadeIn(spring(dampingRatio = 1f, stiffness = 380f)) togetherWith fadeOut(spring(dampingRatio = 1f, stiffness = 380f))
                            },
                            label = "detalle",
                        ) { id -> Detalle(id, onVolver = null) }
                    }
                }
            } else {
                AnimatedContent(
                    targetState = pila.map(::permitido),
                    transitionSpec = { transicion(avanza = targetState.size >= initialState.size) },
                    contentKey = { it.last() },
                    label = "navegacion",
                ) { estado ->
                    when (val destino = estado.last()) {
                        Destino.Principal -> Principal()
                        Destino.Login -> Login()
                        Destino.Maestro -> Maestro(null, { id -> ir(Destino.Detalle(id)) })
                        Destino.Admin -> Admin(null, { id -> ir(Destino.Detalle(id)) })
                        Destino.NuevaCita -> NuevaCita()
                        is Destino.Detalle -> Detalle(destino.citaId, onVolver = ::volver)
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

/** Panel derecho vacío (pantalla ancha) o cita que ya no existe (por ejemplo, si el admin la eliminó). */
@Composable
private fun SinSeleccion(onVolver: (() -> Unit)?) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(Icons.Rounded.TouchApp, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(44.dp))
        Text(
            if (onVolver == null) "Elige una cita de la lista" else "Esta cita ya no está disponible",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 12.dp),
        )
        Text(
            "Aquí verás el lugar, la preparación y la autorización.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (onVolver != null) TextButton(onClick = onVolver) { Text("Volver") }
    }
}
