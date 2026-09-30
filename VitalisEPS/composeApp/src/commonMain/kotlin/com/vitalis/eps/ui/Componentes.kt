package com.vitalis.eps.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Accessibility
import androidx.compose.material.icons.rounded.ChildCare
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.material.icons.rounded.MonitorHeart
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material.icons.rounded.SentimentSatisfied
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vitalis.eps.data.Especialidad
import com.vitalis.eps.data.Estado
import com.vitalis.eps.theme.Vitalis
import com.vitalis.eps.theme.esOscuro

/**
 * Resorte críticamente amortiguado (sin rebote), usado en toda la app
 * para que las transiciones se sientan suaves y se puedan interrumpir.
 */
fun <T> resorte() = spring<T>(dampingRatio = 1f, stiffness = 380f)

/**
 * Hunde levemente el elemento desde el instante en que se toca (no al soltar),
 * para dar respuesta inmediata al dedo.
 */
fun Modifier.presionable(interactionSource: MutableInteractionSource): Modifier = composed {
    val presionado by interactionSource.collectIsPressedAsState()
    val escala by animateFloatAsState(
        targetValue = if (presionado) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 1f, stiffness = if (presionado) 1400f else 500f),
        label = "escala",
    )
    graphicsLayer {
        scaleX = escala
        scaleY = escala
    }
}

@Composable
fun rememberInteraccion() = remember { MutableInteractionSource() }

/** Fondo de "malla": manchas de luz teal y violeta sobre azul noche. */
fun Modifier.fondoMalla(): Modifier = drawBehind {
    drawRect(Brush.linearGradient(listOf(Vitalis.Noche, Vitalis.Indigo), start = Offset.Zero, end = Offset(size.width, size.height)))
    drawCircle(
        Brush.radialGradient(listOf(Vitalis.Teal.copy(alpha = 0.55f), Color.Transparent), center = Offset(size.width * 0.95f, size.height * 0.08f), radius = size.width * 0.75f),
        radius = size.width * 0.75f, center = Offset(size.width * 0.95f, size.height * 0.08f),
    )
    drawCircle(
        Brush.radialGradient(listOf(Vitalis.Violeta.copy(alpha = 0.60f), Color.Transparent), center = Offset(size.width * 0.02f, size.height * 0.92f), radius = size.width * 0.85f),
        radius = size.width * 0.85f, center = Offset(size.width * 0.02f, size.height * 0.92f),
    )
}

/** Logotipo: corazón teal con una línea de pulso. */
@Composable
fun LogoVitalis(modifier: Modifier = Modifier, fondo: Color = Color.White.copy(alpha = 0.12f)) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        drawRoundRect(fondo, cornerRadius = CornerRadius(w * 0.3f))
        val corazon = Path().apply {
            moveTo(w * 0.5f, h * 0.78f)
            cubicTo(w * 0.28f, h * 0.62f, w * 0.18f, h * 0.50f, w * 0.18f, h * 0.38f)
            cubicTo(w * 0.18f, h * 0.26f, w * 0.27f, h * 0.20f, w * 0.36f, h * 0.20f)
            cubicTo(w * 0.43f, h * 0.20f, w * 0.48f, h * 0.24f, w * 0.5f, h * 0.30f)
            cubicTo(w * 0.52f, h * 0.24f, w * 0.57f, h * 0.20f, w * 0.64f, h * 0.20f)
            cubicTo(w * 0.73f, h * 0.20f, w * 0.82f, h * 0.26f, w * 0.82f, h * 0.38f)
            cubicTo(w * 0.82f, h * 0.50f, w * 0.72f, h * 0.62f, w * 0.5f, h * 0.78f)
            close()
        }
        drawPath(corazon, Vitalis.Teal)
        val pulso = Path().apply {
            moveTo(w * 0.14f, h * 0.46f)
            lineTo(w * 0.34f, h * 0.46f)
            lineTo(w * 0.42f, h * 0.32f)
            lineTo(w * 0.52f, h * 0.62f)
            lineTo(w * 0.60f, h * 0.42f)
            lineTo(w * 0.65f, h * 0.46f)
            lineTo(w * 0.86f, h * 0.46f)
        }
        drawPath(pulso, Vitalis.Noche, style = Stroke(width = w * 0.065f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

/**
 * Línea de electrocardiograma que cruza la portada. Un destello recorre la línea
 * de forma continua; si [animada] es falso se dibuja quieta.
 */
@Composable
fun LineaPulso(modifier: Modifier = Modifier, color: Color = Vitalis.Teal, animada: Boolean = true) {
    val progreso = if (animada) {
        val transicion = rememberInfiniteTransition(label = "pulso")
        val p by transicion.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(2600, easing = LinearEasing), RepeatMode.Restart),
            label = "avance",
        )
        p
    } else {
        0.62f
    }
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val base = h * 0.6f
        val puntos = listOf(
            0f to 0f, 0.30f to 0f, 0.34f to -0.10f, 0.37f to 0.05f, 0.40f to 0f,
            0.46f to 0f, 0.49f to -0.55f, 0.53f to 0.45f, 0.56f to -0.15f, 0.58f to 0f,
            0.66f to 0f, 0.70f to -0.14f, 0.74f to 0f, 1f to 0f,
        )
        val linea = Path()
        puntos.forEachIndexed { i, (x, y) ->
            val px = w * x
            val py = base + y * h
            if (i == 0) linea.moveTo(px, py) else linea.lineTo(px, py)
        }
        drawPath(linea, color.copy(alpha = 0.18f), style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(
            linea,
            Brush.horizontalGradient(
                0f to color.copy(alpha = 0.15f),
                (progreso - 0.25f).coerceIn(0f, 1f) to color.copy(alpha = 0.35f),
                progreso to color,
                (progreso + 0.02f).coerceIn(0f, 1f) to color.copy(alpha = 0.35f),
                1f to color.copy(alpha = 0.15f),
            ),
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

fun iconoEspecialidad(e: Especialidad): ImageVector = when (e) {
    Especialidad.MedicinaGeneral -> Icons.Rounded.MedicalServices
    Especialidad.Odontologia -> Icons.Rounded.SentimentSatisfied
    Especialidad.Psicologia -> Icons.Rounded.Psychology
    Especialidad.Cardiologia -> Icons.Rounded.MonitorHeart
    Especialidad.Laboratorio -> Icons.Rounded.Science
    Especialidad.Oftalmologia -> Icons.Rounded.Visibility
    Especialidad.Dermatologia -> Icons.Rounded.Face
    Especialidad.Pediatria -> Icons.Rounded.ChildCare
    Especialidad.Fisioterapia -> Icons.Rounded.Accessibility
}

/** Cuadro redondeado con el ícono de la especialidad sobre su color suave. */
@Composable
fun IconoEspecialidad(e: Especialidad, tamano: Dp = 40.dp, modifier: Modifier = Modifier) {
    Box(
        modifier.size(tamano).clip(RoundedCornerShape(tamano * 0.32f)).background(e.color.copy(alpha = 0.14f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(iconoEspecialidad(e), contentDescription = null, tint = e.color, modifier = Modifier.size(tamano * 0.52f))
    }
}

/** Colores (fondo, texto) de cada estado, ajustados para modo claro u oscuro. */
@Composable
fun colorEstado(estado: Estado): Pair<Color, Color> {
    if (esOscuro) {
        val texto = when (estado) {
            Estado.Confirmada -> Vitalis.Teal
            Estado.PorConfirmar -> Vitalis.Ambar
            Estado.Atendida -> Color(0xFFA5B4FC)
            Estado.Cancelada -> Color(0xFFFF8A90)
        }
        return texto.copy(alpha = 0.16f) to texto
    }
    return when (estado) {
        Estado.Confirmada -> Vitalis.TealClaro to Vitalis.TealOscuro
        Estado.PorConfirmar -> Vitalis.AmbarClaro to Color(0xFFB45309)
        Estado.Atendida -> Color(0xFFE6ECF8) to Vitalis.Indigo
        Estado.Cancelada -> Vitalis.CoralClaro to Color(0xFFC0343B)
    }
}

/** Etiqueta de estado de una cita, con un punto de color. */
@Composable
fun ChipEstado(estado: Estado, modifier: Modifier = Modifier) {
    val (fondo, texto) = colorEstado(estado)
    Row(
        modifier.clip(RoundedCornerShape(50)).background(fondo).padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(6.dp).clip(CircleShape).background(texto))
        Spacer(Modifier.width(6.dp))
        Text(estado.etiqueta, style = MaterialTheme.typography.labelMedium, color = texto)
    }
}

/** Círculo con las iniciales del profesional. */
@Composable
fun Avatar(iniciales: String, color: Color, tamano: Dp = 44.dp, modifier: Modifier = Modifier) {
    Box(
        modifier.size(tamano).clip(CircleShape).background(Brush.linearGradient(listOf(color, color.copy(alpha = 0.7f)))),
        contentAlignment = Alignment.Center,
    ) {
        Text(iniciales, style = MaterialTheme.typography.titleSmall, color = Color.White)
    }
}

/** Botón sol/luna para pasar de modo claro a oscuro y viceversa. */
@Composable
fun BotonTema(onCambiar: () -> Unit, tinta: Color = MaterialTheme.colorScheme.onSurface) {
    IconButton(onClick = onCambiar) {
        Icon(
            if (esOscuro) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
            contentDescription = if (esOscuro) "Cambiar a modo claro" else "Cambiar a modo oscuro",
            tint = tinta,
        )
    }
}

/** Diálogo para ver el estado de la conexión y cambiar la dirección de la API de XAMPP. */
@Composable
fun DialogoServidor(
    urlActual: String,
    origen: Origen,
    mensaje: String?,
    onProbar: (String) -> Unit,
    onCerrar: () -> Unit,
) {
    var url by remember { mutableStateOf(urlActual) }
    AlertDialog(
        onDismissRequest = onCerrar,
        icon = { Icon(Icons.Rounded.Storage, contentDescription = null) },
        title = { Text("Servidor XAMPP") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    when (origen) {
                        Origen.Servidor -> "Conectado. Las citas se leen y se guardan en MySQL."
                        Origen.Conectando -> "Probando la conexión…"
                        Origen.Ejemplo -> mensaje ?: "Sin conexión. Se muestran datos de ejemplo."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                )
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("Dirección de la API") },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    "Emulador: http://10.0.2.2/vitalis_api\nCelular: http://IP-del-PC/vitalis_api (misma red Wi-Fi)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onProbar(url.trim()) }, enabled = origen != Origen.Conectando) { Text("Probar conexión") }
        },
        dismissButton = { TextButton(onClick = onCerrar) { Text("Cerrar") } },
    )
}
