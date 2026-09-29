package com.cumbrecafe.app.ui

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
import androidx.compose.material.icons.rounded.Autorenew
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.LocalCafe
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cumbrecafe.app.data.Empresa
import com.cumbrecafe.app.data.TipoContacto
import com.cumbrecafe.app.theme.Cumbre

/** Vista Principal (Main): pantalla de entrada con la información de la empresa. */
@Composable
fun PantallaPrincipal(onVerCatalogo: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        Portada(onVerCatalogo)
        Column(
            Modifier.padding(horizontal = 20.dp).padding(top = 28.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            Seccion("Quiénes somos") {
                Text(
                    Empresa.QUIENES_SOMOS,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Cifras()
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaProposito(Icons.Rounded.Flag, "Misión", Empresa.MISION, Modifier.weight(1f))
                TarjetaProposito(Icons.Rounded.Visibility, "Visión", Empresa.VISION, Modifier.weight(1f))
            }
            Seccion("Lo que hacemos") { Servicios() }
            Seccion("Visítanos") { Contacto() }
            PiePagina()
        }
        Spacer(Modifier.navigationBarsPadding().height(24.dp))
    }
}

@Composable
private fun Portada(onVerCatalogo: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp))
            .background(Brush.verticalGradient(listOf(Cumbre.Espresso, Cumbre.Tostado, Cumbre.Cacao))),
    ) {
        MontanasDeFondo(Modifier.matchParentSize())
        Column(
            Modifier
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(top = 28.dp, bottom = 32.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LogoCumbre(Modifier.size(52.dp))
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(
                        "CUMBRE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Cumbre.CarameloClaro,
                    )
                    Text(
                        "Tostadores desde 2016",
                        style = MaterialTheme.typography.bodySmall,
                        color = Cumbre.Crema.copy(alpha = 0.7f),
                    )
                }
            }
            Spacer(Modifier.height(44.dp))
            Text(Empresa.NOMBRE, style = MaterialTheme.typography.displaySmall, color = Cumbre.Crema)
            Spacer(Modifier.height(10.dp))
            Text(
                Empresa.ESLOGAN,
                style = MaterialTheme.typography.bodyLarge,
                color = Cumbre.Crema.copy(alpha = 0.82f),
            )
            Spacer(Modifier.height(28.dp))
            val interaccion = rememberInteraccion()
            Button(
                onClick = onVerCatalogo,
                interactionSource = interaccion,
                modifier = Modifier.fillMaxWidth().height(56.dp).presionable(interaccion),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Cumbre.Caramelo, contentColor = Cumbre.Espresso),
            ) {
                Icon(Icons.Rounded.LocalCafe, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(10.dp))
                Text("Explorar catálogo", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.weight(1f))
                Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp))
            }
        }
    }
}

/** Siluetas de cordillera en capas, detrás del texto de la portada. */
@Composable
private fun MontanasDeFondo(modifier: Modifier) {
    androidx.compose.foundation.Canvas(modifier) {
        val w = size.width
        val h = size.height
        fun capa(alturas: List<Float>, color: Color) {
            val p = androidx.compose.ui.graphics.Path()
            p.moveTo(0f, h)
            alturas.forEachIndexed { i, y -> p.lineTo(w * i / (alturas.size - 1), h * y) }
            p.lineTo(w, h)
            p.close()
            drawPath(p, color)
        }
        drawCircle(Cumbre.Caramelo.copy(alpha = 0.16f), radius = w * 0.30f, center = androidx.compose.ui.geometry.Offset(w * 0.86f, h * 0.18f))
        capa(listOf(0.52f, 0.40f, 0.47f, 0.30f, 0.42f, 0.34f, 0.46f), Color.White.copy(alpha = 0.035f))
        capa(listOf(0.70f, 0.58f, 0.64f, 0.52f, 0.60f, 0.55f, 0.66f), Color.White.copy(alpha = 0.045f))
        capa(listOf(0.86f, 0.78f, 0.82f, 0.74f, 0.80f, 0.76f, 0.84f), Color.Black.copy(alpha = 0.10f))
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
    Row(
        Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.large)
            .padding(vertical = 18.dp),
    ) {
        Empresa.cifras.forEachIndexed { i, cifra ->
            if (i > 0) {
                Box(Modifier.width(1.dp).height(44.dp).background(MaterialTheme.colorScheme.outlineVariant).align(Alignment.CenterVertically))
            }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(cifra.valor, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                Text(
                    cifra.etiqueta,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun TarjetaProposito(icono: ImageVector, titulo: String, texto: String, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = if (titulo == "Misión") Cumbre.MontanaClaro else MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Column(Modifier.padding(16.dp)) {
            Box(
                Modifier.size(36.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(19.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(titulo, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(6.dp))
            Text(texto, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun Servicios() {
    val iconos = listOf(Icons.Rounded.LocalCafe, Icons.Rounded.Autorenew, Icons.Rounded.School, Icons.Rounded.Storefront)
    Column(
        Modifier
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.large),
    ) {
        Empresa.servicios.forEachIndexed { i, servicio ->
            if (i > 0) HorizontalDivider(Modifier.padding(start = 68.dp), color = MaterialTheme.colorScheme.outlineVariant)
            Row(Modifier.padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(38.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(iconos[i], contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(servicio.titulo, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                    Text(servicio.descripcion, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun Contacto() {
    Surface(shape = MaterialTheme.shapes.large, color = Cumbre.Tostado) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Empresa.contacto.forEach { dato ->
                val icono = when (dato.tipo) {
                    TipoContacto.Direccion -> Icons.Rounded.Place
                    TipoContacto.Telefono -> Icons.Rounded.Phone
                    TipoContacto.Correo -> Icons.Rounded.Email
                    TipoContacto.Horario -> Icons.Rounded.Schedule
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icono, contentDescription = null, tint = Cumbre.CarameloClaro, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(14.dp))
                    Text(dato.valor, style = MaterialTheme.typography.bodyMedium, color = Cumbre.Crema)
                }
            }
            HorizontalDivider(color = Cumbre.Crema.copy(alpha = 0.15f))
            Text(Empresa.NIT, style = MaterialTheme.typography.bodySmall, color = Cumbre.Crema.copy(alpha = 0.6f))
        }
    }
}

@Composable
private fun PiePagina() {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Guía 7 · Vistas Maestro–Detalle",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            Empresa.equipo.joinToString(" · "),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
