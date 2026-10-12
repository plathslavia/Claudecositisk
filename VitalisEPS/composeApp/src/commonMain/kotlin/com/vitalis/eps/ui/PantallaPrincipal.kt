package com.vitalis.eps.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material3.TextButton
import com.vitalis.eps.data.Rol
import com.vitalis.eps.data.Sesion
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.vitalis.eps.data.Cita
import com.vitalis.eps.data.Empresa
import com.vitalis.eps.data.TipoContacto
import com.vitalis.eps.data.TipoServicio
import com.vitalis.eps.theme.Vitalis
import com.vitalis.eps.theme.colorTarjeta
import com.vitalis.eps.theme.esOscuro

/** Vista Principal (Main): pantalla de entrada con la información de la EPS. */
@Composable
fun PantallaPrincipal(
    sesion: Sesion?,
    proxima: Cita?,
    onAbrir: () -> Unit,
    onCambiarTema: () -> Unit,
    onElegirEstilo: () -> Unit,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        Portada(sesion, proxima, onAbrir, onCambiarTema, onElegirEstilo, onCerrarSesion)
        Column(
            Modifier.padding(horizontal = 20.dp).padding(top = 24.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            Cifras()
            Seccion("Quiénes somos") {
                Text(Empresa.QUIENES_SOMOS, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaProposito(Icons.Rounded.Flag, "Misión", Empresa.MISION, oscura = true, modifier = Modifier.weight(1f))
                TarjetaProposito(Icons.Rounded.Visibility, "Visión", Empresa.VISION, oscura = false, modifier = Modifier.weight(1f))
            }
            Seccion("Nuestros servicios") { Servicios() }
            Seccion("Canales de atención") { Canales() }
            PiePagina()
        }
        Spacer(Modifier.navigationBarsPadding().height(24.dp))
    }
}

@Composable
private fun Portada(
    sesion: Sesion?,
    proxima: Cita?,
    onAbrir: () -> Unit,
    onCambiarTema: () -> Unit,
    onElegirEstilo: () -> Unit,
    onCerrarSesion: () -> Unit,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
            .fondoMalla(Vitalis),
    ) {
        LineaPulso(Modifier.padding(top = 96.dp).fillMaxWidth().height(110.dp))
        Column(Modifier.statusBarsPadding().padding(horizontal = 22.dp).padding(top = 20.dp, bottom = 26.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LogoVitalis(Modifier.size(46.dp))
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(Empresa.NOMBRE, style = MaterialTheme.typography.titleLarge, color = Color.White)
                    Text("Régimen contributivo", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.6f))
                }
                if (sesion != null) {
                    Box(
                        Modifier.clip(RoundedCornerShape(50)).background(Color.White.copy(alpha = 0.12f))
                            .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(50))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text(
                            if (sesion.rol == Rol.Admin) "Administrador" else "Hola, ${sesion.primerNombre}",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                        )
                    }
                }
                BotonEstilo(onElegirEstilo, tinta = Color.White)
                BotonTema(onCambiarTema, tinta = Color.White)
            }
            Spacer(Modifier.height(56.dp))
            Text(
                buildAnnotatedString {
                    append("Tu salud,\n")
                    withStyle(SpanStyle(color = Vitalis.Acento)) { append("a tiempo.") }
                },
                style = MaterialTheme.typography.displayMedium,
                color = Color.White,
            )
            Spacer(Modifier.height(12.dp))
            Text(Empresa.ESLOGAN, style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.78f))
            Spacer(Modifier.height(24.dp))
            when {
                sesion == null -> TarjetaPortada(
                    etiqueta = "TUS CITAS EN UN SOLO LUGAR",
                    textoBoton = "Iniciar sesión",
                    iconoBoton = Icons.AutoMirrored.Rounded.Login,
                    onClick = onAbrir,
                ) {
                    Text(
                        "Ingresa con tu documento para ver, confirmar o cancelar tus citas médicas.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f),
                    )
                }
                sesion.rol == Rol.Admin -> TarjetaPortada(
                    etiqueta = "PANEL DE ADMINISTRACIÓN",
                    textoBoton = "Abrir panel",
                    iconoBoton = Icons.Rounded.AdminPanelSettings,
                    onClick = onAbrir,
                ) {
                    Text(
                        "Agenda citas para los afiliados, agrega profesionales y lleva el control de la red.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f),
                    )
                }
                proxima != null -> TarjetaProxima(proxima, onAbrir)
                else -> TarjetaPortada(
                    etiqueta = "TUS CITAS",
                    textoBoton = "Ver mis citas",
                    iconoBoton = Icons.Rounded.CalendarMonth,
                    onClick = onAbrir,
                ) {
                    Text("No tienes citas próximas.", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.85f))
                }
            }
            if (sesion != null) {
                TextButton(onClick = onCerrarSesion, modifier = Modifier.align(Alignment.End).padding(top = 6.dp)) {
                    Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    val quien = if (sesion.sinConexion) "${sesion.nombre} · sin conexión" else sesion.nombre
                    Text("Cerrar sesión ($quien)", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.8f))
                }
            }
        }
    }
}

/** Tarjeta translúcida de la portada, con una etiqueta, un contenido y un botón de acción. */
@Composable
private fun TarjetaPortada(
    etiqueta: String,
    textoBoton: String,
    iconoBoton: ImageVector,
    onClick: () -> Unit,
    contenido: @Composable () -> Unit,
) {
    val forma = RoundedCornerShape(26.dp)
    Column(
        Modifier
            .fillMaxWidth()
            .clip(forma)
            .background(Color.White.copy(alpha = 0.10f))
            .border(1.dp, Color.White.copy(alpha = 0.18f), forma)
            .padding(16.dp),
    ) {
        Text(etiqueta, style = MaterialTheme.typography.labelSmall, color = Vitalis.Acento)
        Spacer(Modifier.height(10.dp))
        contenido()
        Spacer(Modifier.height(16.dp))
        val interaccion = rememberInteraccion()
        Button(
            onClick = onClick,
            interactionSource = interaccion,
            modifier = Modifier.fillMaxWidth().height(54.dp).presionable(interaccion),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Vitalis.Acento, contentColor = Vitalis.SobreAcento),
        ) {
            Icon(iconoBoton, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Text(textoBoton, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp))
        }
    }
}

/** La próxima cita del afiliado, con el acceso a la vista Maestro. */
@Composable
private fun TarjetaProxima(cita: Cita, onVerCitas: () -> Unit) {
    TarjetaPortada("TU PRÓXIMA CITA", "Ver mis citas", Icons.Rounded.CalendarMonth, onVerCitas) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                Modifier.clip(RoundedCornerShape(16.dp)).background(Color.White).padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(cita.fecha.mesCorto.uppercase(), style = MaterialTheme.typography.labelSmall, color = Vitalis.Peligro)
                Text("${cita.fecha.dia}", style = MaterialTheme.typography.headlineMedium, color = Vitalis.Base)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(cita.especialidad.nombre, style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text("${cita.fecha.diaSemana} · ${cita.hora}", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                Text("${cita.profesional} · ${cita.sede}", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
private fun Seccion(titulo: String, contenido: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(titulo, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)
        contenido()
    }
}

@Composable
private fun Cifras() {
    val acentos = if (esOscuro) listOf(Vitalis.Acento, Color(0xFFA99DFF), Color(0xFF8FB4FF)) else listOf(Vitalis.AcentoOscuro, Vitalis.Acento2, Vitalis.Base2)
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Empresa.cifras.forEachIndexed { i, cifra ->
            Column(
                Modifier
                    .weight(1f)
                    .clip(MaterialTheme.shapes.large)
                    .background(colorTarjeta)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.large)
                    .padding(vertical = 16.dp, horizontal = 12.dp),
            ) {
                Text(cifra.valor, style = MaterialTheme.typography.headlineMedium, color = acentos[i])
                Text(cifra.etiqueta, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun TarjetaProposito(icono: ImageVector, titulo: String, texto: String, oscura: Boolean, modifier: Modifier) {
    val fondo = when {
        oscura && esOscuro -> Vitalis.Base2
        oscura -> Vitalis.Base
        esOscuro -> MaterialTheme.colorScheme.tertiaryContainer
        else -> Vitalis.Acento2Claro
    }
    val tinta = if (oscura || esOscuro) Color.White else Vitalis.Tinta
    Column(modifier.clip(MaterialTheme.shapes.large).background(fondo).padding(16.dp)) {
        Box(
            Modifier.size(36.dp).clip(CircleShape).background(if (oscura) Vitalis.Acento else Vitalis.Acento2),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icono, contentDescription = null, tint = if (oscura) Vitalis.Base else Color.White, modifier = Modifier.size(19.dp))
        }
        Spacer(Modifier.height(12.dp))
        Text(titulo, style = MaterialTheme.typography.titleLarge, color = tinta)
        Spacer(Modifier.height(6.dp))
        Text(texto, style = MaterialTheme.typography.bodySmall, color = tinta.copy(alpha = 0.72f))
    }
}

@Composable
private fun Servicios() {
    val filas = Empresa.servicios.chunked(2)
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        filas.forEach { fila ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                fila.forEach { servicio ->
                    val (icono, color) = when (servicio.tipo) {
                        TipoServicio.Consulta -> Icons.Rounded.MedicalServices to Vitalis.AcentoOscuro
                        TipoServicio.Telemedicina -> Icons.Rounded.Videocam to Vitalis.Acento2
                        TipoServicio.Laboratorio -> Icons.Rounded.Science to Vitalis.Aviso
                        TipoServicio.Prevencion -> Icons.Rounded.HealthAndSafety to Vitalis.Peligro
                    }
                    Column(
                        Modifier
                            .weight(1f)
                            .clip(MaterialTheme.shapes.large)
                            .background(colorTarjeta)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.large)
                            .padding(14.dp),
                    ) {
                        Box(
                            Modifier.size(38.dp).clip(RoundedCornerShape(12.dp)).background(color.copy(alpha = 0.13f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(servicio.titulo, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(Modifier.height(2.dp))
                        Text(servicio.descripcion, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun Canales() {
    Column(
        Modifier.fillMaxWidth().clip(MaterialTheme.shapes.large).fondoMalla(Vitalis).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Empresa.canales.forEach { dato ->
            val icono = when (dato.tipo) {
                TipoContacto.Linea -> Icons.Rounded.Phone
                TipoContacto.WhatsApp -> Icons.AutoMirrored.Rounded.Chat
                TipoContacto.Correo -> Icons.Rounded.Email
                TipoContacto.Sede -> Icons.Rounded.LocationOn
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(34.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(icono, contentDescription = null, tint = Vitalis.Acento, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(14.dp))
                Text(dato.valor, style = MaterialTheme.typography.bodyMedium, color = Color.White)
            }
        }
        HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
        Text(Empresa.NIT, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.6f))
    }
}

@Composable
private fun PiePagina() {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Guía 7 · Vistas Maestro–Detalle", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Text(
            Empresa.equipo.joinToString(" · "),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
