package com.cumbrecafe.app.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.cumbrecafe.app.data.Cafe
import com.cumbrecafe.app.theme.Cumbre
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

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

/** Logotipo de la marca: dos picos de montaña con nieve y un sol de caramelo detrás. */
@Composable
fun LogoCumbre(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        drawRoundRect(
            brush = Brush.linearGradient(listOf(Cumbre.CarameloClaro, Cumbre.Caramelo)),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.28f),
        )
        drawCircle(Cumbre.Crema.copy(alpha = 0.55f), radius = w * 0.13f, center = Offset(w * 0.68f, h * 0.34f))
        val atras = Path().apply {
            moveTo(w * 0.14f, h * 0.78f)
            lineTo(w * 0.40f, h * 0.36f)
            lineTo(w * 0.62f, h * 0.78f)
            close()
        }
        drawPath(atras, Cumbre.Cacao)
        val frente = Path().apply {
            moveTo(w * 0.30f, h * 0.78f)
            lineTo(w * 0.60f, h * 0.26f)
            lineTo(w * 0.88f, h * 0.78f)
            close()
        }
        drawPath(frente, Cumbre.Espresso)
        val nieve = Path().apply {
            moveTo(w * 0.60f, h * 0.26f)
            lineTo(w * 0.525f, h * 0.39f)
            lineTo(w * 0.57f, h * 0.365f)
            lineTo(w * 0.60f, h * 0.40f)
            lineTo(w * 0.635f, h * 0.365f)
            lineTo(w * 0.675f, h * 0.39f)
            close()
        }
        drawPath(nieve, Cumbre.Crema)
    }
}

/**
 * Arte de la bolsa de cada café: degradado propio del origen con curvas de nivel,
 * como un mapa topográfico de la montaña donde se cultivó.
 */
@Composable
fun ArteCafe(cafe: Cafe, modifier: Modifier = Modifier, lineas: Int = 7) {
    Canvas(modifier) {
        drawRect(Brush.linearGradient(listOf(cafe.colorA, cafe.colorB), start = Offset.Zero, end = Offset(size.width, size.height)))
        val semilla = cafe.id.hashCode()
        val cx = size.width * (0.55f + (semilla % 7) / 40f)
        val cy = size.height * (0.62f + (semilla % 5) / 30f)
        curvasDeNivel(Offset(cx, cy), size, lineas, semilla)
    }
}

private fun DrawScope.curvasDeNivel(centro: Offset, area: Size, lineas: Int, semilla: Int) {
    val base = maxOf(area.width, area.height)
    for (i in 1..lineas) {
        val radio = base * 0.09f * i
        val camino = Path()
        val pasos = 72
        for (p in 0..pasos) {
            val t = (p.toFloat() / pasos) * 2f * PI.toFloat()
            val ondula = 1f + 0.08f * sin(3f * t + semilla * 0.37f + i * 0.6f) + 0.05f * cos(5f * t + i)
            val x = centro.x + cos(t) * radio * ondula * 1.25f
            val y = centro.y + sin(t) * radio * ondula * 0.85f
            if (p == 0) camino.moveTo(x, y) else camino.lineTo(x, y)
        }
        camino.close()
        drawPath(
            camino,
            color = Color.White.copy(alpha = 0.10f + 0.02f * (lineas - i)),
            style = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

/** Etiqueta pequeña con fondo suave, usada para notas de cata y datos rápidos. */
@Composable
fun Pastilla(
    texto: String,
    modifier: Modifier = Modifier,
    fondo: Color = MaterialTheme.colorScheme.secondaryContainer,
    color: Color = MaterialTheme.colorScheme.onSecondaryContainer,
) {
    Box(
        modifier
            .clip(RoundedCornerShape(50))
            .background(fondo)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(texto, style = MaterialTheme.typography.labelMedium, color = color)
    }
}
