package com.vitalis.eps.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EventAvailable
import androidx.compose.material.icons.rounded.EventBusy
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vitalis.eps.data.Cita
import com.vitalis.eps.data.Estado
import com.vitalis.eps.data.Modalidad
import com.vitalis.eps.data.iniciales
import com.vitalis.eps.data.pesos
import com.vitalis.eps.theme.Vitalis
import com.vitalis.eps.theme.colorTarjeta
import com.vitalis.eps.theme.esOscuro

/**
 * Vista Detalle: toda la información de la cita elegida en la vista Maestro.
 * [onVolver] es nulo cuando el detalle se muestra al lado de la lista (pantallas anchas).
 */
@Composable
fun PantallaDetalle(
    cita: Cita,
    onVolver: (() -> Unit)?,
    onCambiarEstado: (Estado) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Con el celular de lado hay poca altura: el encabezado se hace más bajo.
        val compacto = maxHeight < 520.dp
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Encabezado(cita, onVolver, compacto)
            Column(
                Modifier.widthIn(max = 720.dp).padding(horizontal = 20.dp).padding(top = 22.dp, bottom = 140.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Lugar(cita)
                Bloque("Motivo de la consulta") {
                    Text(cita.motivo, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (cita.estado.esProxima) {
                    Bloque("Antes de tu cita") { ListaPreparacion(cita) }
                }
                Bloque("Autorización") { Autorizacion(cita) }
            }
        }
        BarraAcciones(cita, onCambiarEstado, Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun Encabezado(cita: Cita, onVolver: (() -> Unit)?, compacto: Boolean) {
    val color = cita.especialidad.color
    Box(Modifier.fillMaxWidth().height(if (compacto) 262.dp else 372.dp)) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(if (compacto) 208.dp else 318.dp)
                .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                .background(Brush.linearGradient(listOf(color, Vitalis.Indigo, Vitalis.Noche))),
        ) {
            LineaPulso(
                Modifier.padding(top = if (compacto) 30.dp else 70.dp).fillMaxWidth().height(90.dp),
                color = Color.White,
                animada = cita.estado.esProxima,
            )
            Row(
                Modifier.statusBarsPadding().padding(horizontal = 12.dp, vertical = 8.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (onVolver != null) {
                    IconButton(
                        onClick = onVolver,
                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White.copy(alpha = 0.16f), contentColor = Color.White),
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver a mis citas")
                    }
                }
                Spacer(Modifier.weight(1f))
                ChipEstado(cita.estado, Modifier.padding(end = 8.dp))
            }
            Column(Modifier.align(Alignment.BottomStart).padding(start = 24.dp, end = 24.dp, bottom = 62.dp)) {
                Text(
                    if (cita.modalidad == Modalidad.Telemedicina) "CITA POR VIDEOLLAMADA" else "CITA PRESENCIAL",
                    style = MaterialTheme.typography.labelSmall,
                    color = Vitalis.Teal,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    cita.especialidad.nombre,
                    style = if (compacto) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.displaySmall,
                    color = Color.White,
                )
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Avatar(iniciales(cita.profesional), color, tamano = 38.dp)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(cita.profesional, style = MaterialTheme.typography.titleSmall, color = Color.White)
                        Text(cita.registro, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.65f))
                    }
                }
            }
        }
        Tiquete(cita, Modifier.align(Alignment.BottomCenter).widthIn(max = 680.dp).padding(horizontal = 20.dp))
    }
}

/** Tarjeta que se monta sobre el encabezado con fecha, hora y duración. */
@Composable
private fun Tiquete(cita: Cita, modifier: Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .shadow(18.dp, RoundedCornerShape(24.dp), ambientColor = Vitalis.Noche.copy(alpha = 0.25f), spotColor = Vitalis.Noche.copy(alpha = 0.25f))
            .clip(RoundedCornerShape(24.dp))
            .background(colorTarjeta)
            .height(IntrinsicSize.Min)
            .padding(vertical = 16.dp),
    ) {
        val datos = listOf("Fecha" to cita.fecha.corta, "Hora" to cita.hora, "Duración" to "${cita.duracionMin} min")
        datos.forEachIndexed { i, (etiqueta, valor) ->
            if (i > 0) Box(Modifier.width(1.dp).fillMaxHeight().background(MaterialTheme.colorScheme.outlineVariant))
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(etiqueta.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                Text(valor, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun Lugar(cita: Cita) {
    val virtual = cita.modalidad == Modalidad.Telemedicina
    Row(
        Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(colorTarjeta)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.large)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(46.dp).clip(RoundedCornerShape(14.dp)).background((if (virtual) Vitalis.Violeta else Vitalis.Teal).copy(alpha = if (esOscuro) 0.2f else 0.14f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                if (virtual) Icons.Rounded.Videocam else Icons.Rounded.LocationOn,
                contentDescription = null,
                tint = if (virtual) Vitalis.Violeta else if (esOscuro) Vitalis.Teal else Vitalis.TealOscuro,
                modifier = Modifier.size(24.dp),
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(cita.sede, style = MaterialTheme.typography.titleMedium)
            Text(cita.lugar, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun Bloque(titulo: String, contenido: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(titulo, style = MaterialTheme.typography.titleLarge)
        contenido()
    }
}

/** Lista de preparación que el afiliado va marcando. */
@Composable
private fun ListaPreparacion(cita: Cita) {
    val hechos = remember(cita.id) { mutableStateListOf(*Array(cita.preparacion.size) { false }) }
    Column(
        Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(colorTarjeta)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.large),
    ) {
        cita.preparacion.forEachIndexed { i, paso ->
            val listo = hechos[i]
            val fondo by animateColorAsState(if (listo) Vitalis.Teal else Color.Transparent, resorte(), label = "check")
            val borde by animateColorAsState(if (listo) Vitalis.Teal else MaterialTheme.colorScheme.outline, resorte(), label = "borde")
            Row(
                Modifier.fillMaxWidth().clickable { hechos[i] = !listo }.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier.size(24.dp).clip(CircleShape).background(fondo).border(2.dp, borde, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    if (listo) Icon(Icons.Rounded.Check, contentDescription = null, tint = Vitalis.Noche, modifier = Modifier.size(16.dp))
                }
                Spacer(Modifier.width(14.dp))
                Text(
                    paso,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (listo) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun Autorizacion(cita: Cita) {
    val filas = listOf(
        Triple(Icons.Rounded.Receipt, "Número de autorización", cita.autorizacion),
        Triple(Icons.Rounded.Payments, "Cuota moderadora", pesos(cita.cuotaModeradora)),
        Triple(Icons.Rounded.Badge, "Registro del profesional", cita.registro),
    )
    Column(
        Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(colorTarjeta)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.large),
    ) {
        filas.forEachIndexed { i, (icono, etiqueta, valor) ->
            if (i > 0) Box(Modifier.padding(start = 56.dp).fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outlineVariant))
            FilaDato(icono, etiqueta, valor)
        }
    }
}

@Composable
private fun FilaDato(icono: ImageVector, etiqueta: String, valor: String) {
    Row(Modifier.padding(horizontal = 16.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(18.dp))
        Text(etiqueta, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(valor, style = MaterialTheme.typography.titleSmall)
    }
}

/** Barra inferior: las acciones cambian según el estado de la cita. */
@Composable
private fun BarraAcciones(cita: Cita, onCambiarEstado: (Estado) -> Unit, modifier: Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = colorTarjeta,
        shadowElevation = 18.dp,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    ) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        AnimatedContent(
            targetState = cita.estado,
            transitionSpec = { fadeIn(resorte()) togetherWith fadeOut(resorte()) },
            label = "acciones",
        ) { estado ->
            Row(
                Modifier.navigationBarsPadding().padding(horizontal = 20.dp, vertical = 14.dp).widthIn(max = 720.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                when (estado) {
                    Estado.PorConfirmar -> {
                        OutlinedButton(
                            onClick = { onCambiarEstado(Estado.Cancelada) },
                            modifier = Modifier.height(54.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Vitalis.Coral),
                        ) { Text("Cancelar", style = MaterialTheme.typography.labelLarge) }
                        val interaccion = rememberInteraccion()
                        Button(
                            onClick = { onCambiarEstado(Estado.Confirmada) },
                            interactionSource = interaccion,
                            modifier = Modifier.weight(1f).height(54.dp).presionable(interaccion),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Vitalis.Teal, contentColor = Vitalis.Noche),
                        ) {
                            Icon(Icons.Rounded.EventAvailable, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Confirmar asistencia", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                    Estado.Confirmada -> {
                        Aviso(Icons.Rounded.CheckCircle, "Asistencia confirmada", "Te recordaremos un día antes.", colorEstado(Estado.Confirmada).second, Modifier.weight(1f))
                        OutlinedButton(
                            onClick = { onCambiarEstado(Estado.Cancelada) },
                            modifier = Modifier.height(50.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Vitalis.Coral),
                        ) { Text("Cancelar cita", style = MaterialTheme.typography.labelLarge) }
                    }
                    Estado.Atendida -> Aviso(
                        Icons.Rounded.TaskAlt, "Cita atendida", "El resumen de la consulta ya está en tu historia clínica.",
                        colorEstado(Estado.Atendida).second, Modifier.weight(1f),
                    )
                    Estado.Cancelada -> Aviso(
                        Icons.Rounded.EventBusy, "Cita cancelada", "Puedes pedir una nueva por la línea 24/7 o por WhatsApp.",
                        Vitalis.Coral, Modifier.weight(1f),
                    )
                }
            }
        }
        }
    }
}

@Composable
private fun Aviso(icono: ImageVector, titulo: String, texto: String, color: Color, modifier: Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(titulo, style = MaterialTheme.typography.titleSmall, color = color)
            Text(texto, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
