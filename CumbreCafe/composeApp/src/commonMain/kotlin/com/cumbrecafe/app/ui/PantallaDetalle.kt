package com.cumbrecafe.app.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Agriculture
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.Grass
import androidx.compose.material.icons.rounded.Landscape
import androidx.compose.material.icons.rounded.LocalCafe
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cumbrecafe.app.data.Cafe
import com.cumbrecafe.app.data.etiquetaTueste
import com.cumbrecafe.app.data.miles
import com.cumbrecafe.app.data.precioCop
import com.cumbrecafe.app.theme.Cumbre

/**
 * Vista Detalle: muestra toda la información del café elegido en la vista Maestro.
 * [onVolver] es nulo cuando el detalle se muestra al lado de la lista (pantallas anchas).
 */
@Composable
fun PantallaDetalle(cafe: Cafe, onVolver: (() -> Unit)?, modifier: Modifier = Modifier) {
    var cantidad by remember(cafe.id) { mutableIntStateOf(1) }
    var agregado by remember(cafe.id) { mutableStateOf(false) }

    Box(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Encabezado(cafe, onVolver)
            Column(
                Modifier.widthIn(max = 720.dp).padding(horizontal = 20.dp).padding(top = 20.dp, bottom = 128.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Ficha(cafe)
                Bloque("Notas de cata") {
                    NotasDeCata(cafe)
                }
                Bloque("Nivel de tueste", etiqueta = etiquetaTueste(cafe.tueste)) {
                    BarraTueste(cafe.tueste)
                }
                Bloque("La historia") {
                    Text(cafe.descripcion, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Bloque("Prepáralo en") {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        cafe.metodos.forEach { metodo -> Metodo(metodo, Modifier.weight(1f)) }
                    }
                }
            }
        }
        BarraCompra(
            cafe = cafe,
            cantidad = cantidad,
            agregado = agregado,
            onMenos = { if (cantidad > 1) { cantidad--; agregado = false } },
            onMas = { if (cantidad < 9) { cantidad++; agregado = false } },
            onAgregar = { agregado = true },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun Encabezado(cafe: Cafe, onVolver: (() -> Unit)?) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(320.dp)
            .clip(RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp)),
    ) {
        ArteCafe(cafe, Modifier.matchParentSize(), lineas = 9)
        Box(Modifier.matchParentSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.45f)))))
        Row(
            Modifier.statusBarsPadding().padding(horizontal = 12.dp, vertical = 8.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (onVolver != null) {
                IconButton(
                    onClick = onVolver,
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Black.copy(alpha = 0.28f), contentColor = Color.White),
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver al catálogo")
                }
            }
            Spacer(Modifier.weight(1f))
            Puntaje(cafe.puntaje, Modifier.padding(end = 8.dp))
        }
        Column(Modifier.align(Alignment.BottomStart).padding(24.dp)) {
            Text(
                cafe.region.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = Cumbre.CarameloClaro,
            )
            Spacer(Modifier.height(6.dp))
            Text(cafe.nombre, style = MaterialTheme.typography.displaySmall, color = Color.White)
            Spacer(Modifier.height(6.dp))
            Text(
                "${cafe.finca} · ${cafe.productor}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f),
            )
        }
    }
}

@Composable
private fun Ficha(cafe: Cafe) {
    val datos = listOf(
        Triple(Icons.Rounded.Landscape, "Altitud", "${miles(cafe.altitud)} m"),
        Triple(Icons.Rounded.Grass, "Variedad", cafe.variedad),
        Triple(Icons.Rounded.WaterDrop, "Proceso", cafe.proceso.etiqueta),
        Triple(Icons.Rounded.Agriculture, "Productor", cafe.productor),
    )
    Column(
        Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.large),
    ) {
        datos.chunked(2).forEachIndexed { fila, par ->
            if (fila > 0) Box(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outlineVariant))
            Row(Modifier.height(androidx.compose.foundation.layout.IntrinsicSize.Min)) {
                par.forEachIndexed { i, (icono, etiqueta, valor) ->
                    if (i > 0) Box(Modifier.width(1.dp).fillMaxHeight().background(MaterialTheme.colorScheme.outlineVariant))
                    DatoFicha(icono, etiqueta, valor, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun DatoFicha(icono: ImageVector, etiqueta: String, valor: String, modifier: Modifier) {
    Row(modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(34.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Text(etiqueta, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(valor, style = MaterialTheme.typography.titleSmall, maxLines = 1)
        }
    }
}

@Composable
private fun Bloque(titulo: String, etiqueta: String? = null, contenido: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(titulo, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
            if (etiqueta != null) Pastilla(etiqueta)
        }
        contenido()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NotasDeCata(cafe: Cafe) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        cafe.notas.forEach { nota ->
            Pastilla(nota, fondo = cafe.colorA.copy(alpha = 0.14f), color = cafe.colorB)
        }
    }
}

@Composable
private fun BarraTueste(tueste: Float) {
    val progreso by animateFloatAsState(tueste, animationSpec = resorte(), label = "tueste")
    Column {
        Box(
            Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(50))
                .background(Brush.horizontalGradient(listOf(Color(0xFFE8C9A0), Cumbre.Caramelo, Cumbre.Cacao, Cumbre.Espresso))),
        ) {
            androidx.compose.foundation.layout.BoxWithConstraints(Modifier.matchParentSize()) {
                Box(
                    Modifier
                        .offset(x = (maxWidth - 12.dp) * progreso)
                        .size(12.dp)
                        .shadow(3.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(3.dp, Cumbre.Tostado, CircleShape),
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Row {
            Text("Claro", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.weight(1f))
            Text("Oscuro", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun Metodo(nombre: String, modifier: Modifier) {
    val icono = when (nombre) {
        "Espresso", "Moka", "Greca" -> Icons.Rounded.Coffee
        else -> Icons.Rounded.LocalCafe
    }
    Column(
        modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.medium)
            .padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(icono, contentDescription = null, tint = Cumbre.Caramelo, modifier = Modifier.size(22.dp))
        Spacer(Modifier.height(6.dp))
        Text(nombre, style = MaterialTheme.typography.labelMedium, maxLines = 2, textAlign = TextAlign.Center)
    }
}

/** Barra inferior flotante con precio, cantidad y botón de compra. */
@Composable
private fun BarraCompra(
    cafe: Cafe,
    cantidad: Int,
    agregado: Boolean,
    onMenos: () -> Unit,
    onMas: () -> Unit,
    onAgregar: () -> Unit,
    modifier: Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.97f),
        shadowElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    ) {
        Row(
            Modifier.navigationBarsPadding().padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(precioCop(cafe.precio * cantidad), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                Text("$cantidad × 340 g", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.width(12.dp))
            Row(
                Modifier.clip(RoundedCornerShape(50)).background(MaterialTheme.colorScheme.primaryContainer),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onMenos, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Rounded.Remove, contentDescription = "Quitar una bolsa", modifier = Modifier.size(18.dp))
                }
                Text("$cantidad", style = MaterialTheme.typography.titleSmall)
                IconButton(onClick = onMas, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Rounded.Add, contentDescription = "Agregar una bolsa", modifier = Modifier.size(18.dp))
                }
            }
            Spacer(Modifier.width(12.dp))
            val interaccion = rememberInteraccion()
            Button(
                onClick = onAgregar,
                interactionSource = interaccion,
                modifier = Modifier.weight(1f).height(52.dp).presionable(interaccion),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (agregado) Cumbre.Montana else MaterialTheme.colorScheme.primary,
                ),
            ) {
                Icon(
                    if (agregado) Icons.Rounded.CheckCircle else Icons.Rounded.ShoppingBag,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(if (agregado) "Agregado" else "Agregar", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
