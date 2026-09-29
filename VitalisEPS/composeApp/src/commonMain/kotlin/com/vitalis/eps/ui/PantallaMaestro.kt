package com.vitalis.eps.ui

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
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EventBusy
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Videocam
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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vitalis.eps.data.Agenda
import com.vitalis.eps.data.Cita
import com.vitalis.eps.data.Empresa
import com.vitalis.eps.data.Estado
import com.vitalis.eps.data.Modalidad
import com.vitalis.eps.theme.Vitalis

enum class Filtro(val etiqueta: String) {
    Proximas("Próximas"),
    PorConfirmar("Por confirmar"),
    Historial("Historial"),
    Todas("Todas"),
}

/**
 * Estado de la agenda: búsqueda, filtro, posición de la lista y los cambios de estado
 * que hace el afiliado (confirmar o cancelar). Vive fuera de las pantallas para que
 * el Maestro y el Detalle vean siempre los mismos datos.
 */
class EstadoAgenda {
    var busqueda by mutableStateOf("")
    var filtro by mutableStateOf(Filtro.Proximas)
    val lista = LazyListState()
    private val cambios = mutableStateMapOf<String, Estado>()

    val citas: List<Cita> get() = Agenda.citas.map { c -> cambios[c.id]?.let { c.copy(estado = it) } ?: c }

    fun cita(id: String): Cita? = citas.firstOrNull { it.id == id }
    fun cambiarEstado(id: String, estado: Estado) { cambios[id] = estado }

    val proxima: Cita? get() = citas.filter { it.estado.esProxima }.minByOrNull { it.fecha.orden }

    val resultados: List<Cita>
        get() {
            val texto = busqueda.trim()
            return citas
                .filter { c ->
                    when (filtro) {
                        Filtro.Proximas -> c.estado.esProxima
                        Filtro.PorConfirmar -> c.estado == Estado.PorConfirmar
                        Filtro.Historial -> !c.estado.esProxima
                        Filtro.Todas -> true
                    }
                }
                .filter { c ->
                    texto.isEmpty() || listOf(c.especialidad.nombre, c.profesional, c.sede, c.modalidad.etiqueta)
                        .any { it.contains(texto, ignoreCase = true) }
                }
                .sortedWith(compareBy<Cita> { !it.estado.esProxima }.thenBy { if (it.estado.esProxima) it.fecha.orden else -it.fecha.orden })
        }
}

/** Vista Maestro: lista de citas del afiliado. Tocar una abre su detalle. */
@Composable
fun PantallaMaestro(
    estado: EstadoAgenda,
    seleccionadaId: String?,
    onVolver: () -> Unit,
    onSeleccionar: (Cita) -> Unit,
    modifier: Modifier = Modifier,
) {
    val resultados = estado.resultados
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(Modifier.statusBarsPadding().padding(start = 12.dp, end = 20.dp, top = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onVolver) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver al inicio")
                }
                Spacer(Modifier.weight(1f))
                LogoVitalis(Modifier.size(32.dp), fondo = Vitalis.Noche)
            }
            Column(Modifier.padding(start = 8.dp, top = 4.dp)) {
                Text("Mis citas", style = MaterialTheme.typography.displaySmall)
                Text(
                    "${Empresa.AFILIADO} · Afiliado cotizante",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Resumen(estado.citas, Modifier.padding(horizontal = 20.dp))
        Spacer(Modifier.height(14.dp))
        OutlinedTextField(
            value = estado.busqueda,
            onValueChange = { estado.busqueda = it },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            placeholder = { Text("Buscar médico o especialidad") },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            trailingIcon = {
                if (estado.busqueda.isNotEmpty()) {
                    IconButton(onClick = { estado.busqueda = "" }) {
                        Icon(Icons.Rounded.Close, contentDescription = "Borrar búsqueda")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            textStyle = MaterialTheme.typography.bodyMedium,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedBorderColor = Vitalis.Teal,
            ),
        )
        Spacer(Modifier.height(12.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(Filtro.entries) { opcion ->
                val activo = estado.filtro == opcion
                FilterChip(
                    selected = activo,
                    onClick = { estado.filtro = opcion },
                    label = { Text(opcion.etiqueta) },
                    shape = RoundedCornerShape(50),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.White,
                        selectedContainerColor = Vitalis.Noche,
                        selectedLabelColor = Color.White,
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = activo,
                        borderColor = MaterialTheme.colorScheme.outlineVariant,
                        selectedBorderColor = Vitalis.Noche,
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
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                var mesAnterior = ""
                resultados.forEach { cita ->
                    val mes = "${cita.fecha.mesLargo.replaceFirstChar { it.uppercase() }} ${cita.fecha.anio}"
                    if (mes != mesAnterior) {
                        mesAnterior = mes
                        item(key = "mes-$mes-${cita.id}") {
                            Text(
                                mes.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 4.dp, top = 10.dp),
                            )
                        }
                    }
                    item(key = cita.id) {
                        FilaCita(cita, seleccionada = cita.id == seleccionadaId, onClick = { onSeleccionar(cita) })
                    }
                }
                item { Spacer(Modifier.navigationBarsPadding()) }
            }
        }
    }
}

@Composable
private fun Resumen(citas: List<Cita>, modifier: Modifier) {
    val datos = listOf(
        Triple(citas.count { it.estado == Estado.Confirmada }, "Confirmadas", Vitalis.TealOscuro),
        Triple(citas.count { it.estado == Estado.PorConfirmar }, "Por confirmar", Color(0xFFB45309)),
        Triple(citas.count { it.estado == Estado.Atendida }, "Atendidas", Vitalis.Indigo),
    )
    Row(
        modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(Color.White)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.large)
            .padding(vertical = 12.dp),
    ) {
        datos.forEachIndexed { i, (n, etiqueta, color) ->
            if (i > 0) Box(Modifier.width(1.dp).height(36.dp).background(MaterialTheme.colorScheme.outlineVariant).align(Alignment.CenterVertically))
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$n", style = MaterialTheme.typography.headlineSmall, color = color)
                Text(etiqueta, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun FilaCita(cita: Cita, seleccionada: Boolean, onClick: () -> Unit) {
    val interaccion = rememberInteraccion()
    val borde by animateColorAsState(
        if (seleccionada) Vitalis.Teal else MaterialTheme.colorScheme.outlineVariant,
        animationSpec = resorte(),
        label = "borde",
    )
    val color = cita.especialidad.color
    val pasada = !cita.estado.esProxima
    Row(
        Modifier
            .fillMaxWidth()
            .presionable(interaccion)
            .clip(MaterialTheme.shapes.large)
            .background(Color.White)
            .border(if (seleccionada) 2.dp else 1.dp, borde, MaterialTheme.shapes.large)
            .clickable(interactionSource = interaccion, indication = ripple(), onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            Modifier
                .width(58.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (pasada) MaterialTheme.colorScheme.surfaceVariant else color.copy(alpha = 0.12f))
                .padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val tinta = if (pasada) MaterialTheme.colorScheme.onSurfaceVariant else color
            Text(cita.fecha.diaSemana.uppercase(), style = MaterialTheme.typography.labelSmall, color = tinta)
            Text("${cita.fecha.dia}", style = MaterialTheme.typography.headlineMedium, color = if (pasada) tinta else Vitalis.Noche)
            Text(cita.fecha.mesCorto, style = MaterialTheme.typography.bodySmall, color = tinta)
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconoEspecialidad(cita.especialidad, tamano = 22.dp)
                Spacer(Modifier.width(8.dp))
                Text(
                    cita.especialidad.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(3.dp))
            Text(cita.profesional, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text(cita.hora, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(10.dp))
                Icon(
                    if (cita.modalidad == Modalidad.Telemedicina) Icons.Rounded.Videocam else Icons.Rounded.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(14.dp),
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    cita.sede,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(8.dp))
            ChipEstado(cita.estado)
        }
    }
}

@Composable
private fun SinResultados(modifier: Modifier) {
    Column(modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Rounded.EventBusy, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(44.dp))
        Spacer(Modifier.height(12.dp))
        Text("No hay citas con ese filtro", style = MaterialTheme.typography.titleSmall)
        Text(
            "Prueba con otra especialidad, médico o sede.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
