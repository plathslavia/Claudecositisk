package com.cumbrecafe.app.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Landscape
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cumbrecafe.app.data.Cafe
import com.cumbrecafe.app.data.Catalogo
import com.cumbrecafe.app.data.Proceso
import com.cumbrecafe.app.data.miles
import com.cumbrecafe.app.data.precioCop
import com.cumbrecafe.app.theme.Cumbre

/** Estado de búsqueda y filtro del catálogo; vive fuera de la pantalla para no perderse al volver del detalle. */
class EstadoCatalogo {
    var busqueda by mutableStateOf("")
    var proceso by mutableStateOf<Proceso?>(null)
    val lista = LazyListState()

    val resultados: List<Cafe>
        get() = Catalogo.cafes.filter { cafe ->
            (proceso == null || cafe.proceso == proceso) &&
                (busqueda.isBlank() || listOf(cafe.nombre, cafe.region, cafe.variedad, *cafe.notas.toTypedArray())
                    .any { it.contains(busqueda.trim(), ignoreCase = true) })
        }
}

/** Vista Maestro: lista de cafés con búsqueda y filtro por proceso. Tocar uno abre su detalle. */
@Composable
fun PantallaMaestro(
    estado: EstadoCatalogo,
    seleccionadoId: String?,
    onVolver: () -> Unit,
    onSeleccionar: (Cafe) -> Unit,
    modifier: Modifier = Modifier,
) {
    val resultados = estado.resultados
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            Modifier
                .statusBarsPadding()
                .padding(start = 12.dp, end = 20.dp, top = 8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onVolver) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver al inicio")
                }
                Spacer(Modifier.weight(1f))
                LogoCumbre(Modifier.size(30.dp))
            }
            Column(Modifier.padding(start = 8.dp, top = 6.dp)) {
                Text("Nuestros cafés", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "${Catalogo.cafes.size} orígenes colombianos · tostados cada semana",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = estado.busqueda,
            onValueChange = { estado.busqueda = it },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            placeholder = { Text("Buscar por origen, variedad o nota") },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            trailingIcon = {
                if (estado.busqueda.isNotEmpty()) {
                    IconButton(onClick = { estado.busqueda = "" }) {
                        Icon(Icons.Rounded.Close, contentDescription = "Borrar búsqueda")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            textStyle = MaterialTheme.typography.bodyMedium,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedBorderColor = MaterialTheme.colorScheme.secondary,
            ),
        )
        Spacer(Modifier.height(12.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val opciones = listOf<Proceso?>(null) + Proceso.entries
            items(opciones) { opcion ->
                val activo = estado.proceso == opcion
                FilterChip(
                    selected = activo,
                    onClick = { estado.proceso = opcion },
                    label = { Text(opcion?.etiqueta ?: "Todos") },
                    shape = RoundedCornerShape(50),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = activo,
                        borderColor = MaterialTheme.colorScheme.outlineVariant,
                        selectedBorderColor = MaterialTheme.colorScheme.primary,
                    ),
                )
            }
        }
        if (resultados.isEmpty()) {
            SinResultados(Modifier.fillMaxWidth().weight(1f))
        } else {
            LazyColumn(
                state = estado.lista,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(resultados, key = { it.id }) { cafe ->
                    FilaCafe(cafe, seleccionado = cafe.id == seleccionadoId, onClick = { onSeleccionar(cafe) })
                }
                item { Spacer(Modifier.navigationBarsPadding()) }
            }
        }
    }
}

@Composable
private fun FilaCafe(cafe: Cafe, seleccionado: Boolean, onClick: () -> Unit) {
    val interaccion = rememberInteraccion()
    val borde by animateColorAsState(
        if (seleccionado) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outlineVariant,
        animationSpec = resorte(),
        label = "borde",
    )
    Row(
        Modifier
            .fillMaxWidth()
            .presionable(interaccion)
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(if (seleccionado) 2.dp else 1.dp, borde, MaterialTheme.shapes.large)
            .clickable(interactionSource = interaccion, indication = ripple(), onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(width = 78.dp, height = 96.dp).clip(RoundedCornerShape(14.dp))) {
            ArteCafe(cafe, Modifier.matchParentSize(), lineas = 5)
            Text(
                cafe.proceso.etiqueta.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = Cumbre.Crema,
                modifier = Modifier.align(Alignment.BottomStart).padding(8.dp),
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    cafe.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(8.dp))
                Puntaje(cafe.puntaje)
            }
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Landscape, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    "${cafe.region} · ${miles(cafe.altitud)} m",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                cafe.notas.joinToString(" · "),
                style = MaterialTheme.typography.bodySmall,
                color = Cumbre.Cacao,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(precioCop(cafe.precio), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                Text(" / 340 g", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.weight(1f))
                Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

@Composable
fun Puntaje(puntaje: Double, modifier: Modifier = Modifier) {
    Row(
        modifier
            .clip(RoundedCornerShape(50))
            .background(Cumbre.MontanaClaro)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Rounded.Star, contentDescription = null, tint = Cumbre.Montana, modifier = Modifier.size(12.dp))
        Spacer(Modifier.width(3.dp))
        Text(formatoPuntaje(puntaje), style = MaterialTheme.typography.labelMedium, color = Cumbre.Montana)
    }
}

fun formatoPuntaje(p: Double): String {
    val entero = p.toInt()
    val decimal = ((p - entero) * 10).toInt()
    return if (decimal == 0) "$entero" else "$entero,$decimal"
}

@Composable
private fun SinResultados(modifier: Modifier) {
    Column(modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Rounded.Search, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(40.dp))
        Spacer(Modifier.height(12.dp))
        Text("No encontramos cafés con esa búsqueda", style = MaterialTheme.typography.titleSmall)
        Text(
            "Prueba con otra región, variedad o nota de cata.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
