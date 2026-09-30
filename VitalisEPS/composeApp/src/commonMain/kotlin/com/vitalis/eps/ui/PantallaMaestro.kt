package com.vitalis.eps.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vitalis.eps.data.Cita
import com.vitalis.eps.data.Empresa
import com.vitalis.eps.data.Estado
import com.vitalis.eps.data.Modalidad
import com.vitalis.eps.theme.Vitalis
import com.vitalis.eps.theme.colorTarjeta
import com.vitalis.eps.theme.esOscuro

/**
 * Vista Maestro: lista de citas del afiliado. Tocar una abre su detalle.
 * Todo (encabezado, resumen, búsqueda y lista) va en una sola lista desplazable, para que
 * con el celular de lado se puedan ver todas las citas; los filtros quedan fijos arriba.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PantallaMaestro(
    estado: EstadoAgenda,
    seleccionadaId: String?,
    onVolver: () -> Unit,
    onSeleccionar: (Cita) -> Unit,
    onCambiarTema: () -> Unit,
    onReintentar: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val resultados = estado.resultados
    var dialogoServidor by remember { mutableStateOf(false) }

    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Spacer(Modifier.statusBarsPadding())
        LazyColumn(
            state = estado.lista,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            item(key = "encabezado") {
                Column(Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onVolver) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver al inicio")
                        }
                        Spacer(Modifier.weight(1f))
                        BotonTema(onCambiarTema)
                        Spacer(Modifier.width(4.dp))
                        LogoVitalis(Modifier.size(32.dp), fondo = if (esOscuro) Vitalis.Indigo else Vitalis.Noche)
                        Spacer(Modifier.width(8.dp))
                    }
                    Column(Modifier.padding(start = 8.dp, top = 4.dp, end = 8.dp)) {
                        Text("Mis citas", style = MaterialTheme.typography.displaySmall)
                        Text(
                            "${Empresa.AFILIADO} · Afiliado cotizante",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.height(10.dp))
                        EstadoConexion(estado.origen, onClick = { dialogoServidor = true })
                    }
                }
            }
            item(key = "resumen") {
                Resumen(estado.citas, Modifier.padding(horizontal = 20.dp).padding(top = 16.dp))
            }
            item(key = "buscador") {
                OutlinedTextField(
                    value = estado.busqueda,
                    onValueChange = { estado.busqueda = it },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(top = 14.dp),
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
                        unfocusedContainerColor = colorTarjeta,
                        focusedContainerColor = colorTarjeta,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedBorderColor = Vitalis.Teal,
                    ),
                )
            }
            stickyHeader(key = "filtros") {
                Filtros(estado, Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background).padding(vertical = 12.dp))
            }
            if (resultados.isEmpty()) {
                item(key = "vacio") { SinResultados(Modifier.fillMaxWidth()) }
            } else {
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
                                modifier = Modifier.padding(start = 24.dp, top = 6.dp, bottom = 8.dp),
                            )
                        }
                    }
                    item(key = cita.id) {
                        FilaCita(
                            cita,
                            seleccionada = cita.id == seleccionadaId,
                            onClick = { onSeleccionar(cita) },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp),
                        )
                    }
                }
            }
            item(key = "fin") { Spacer(Modifier.navigationBarsPadding()) }
        }
    }

    if (dialogoServidor) {
        DialogoServidor(
            urlActual = estado.urlServidor,
            origen = estado.origen,
            mensaje = estado.mensaje,
            onProbar = onReintentar,
            onCerrar = { dialogoServidor = false },
        )
    }
}

@Composable
private fun Filtros(estado: EstadoAgenda, modifier: Modifier) {
    LazyRow(modifier, contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(Filtro.entries) { opcion ->
            val activo = estado.filtro == opcion
            FilterChip(
                selected = activo,
                onClick = { estado.filtro = opcion },
                label = { Text(opcion.etiqueta) },
                shape = RoundedCornerShape(50),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = colorTarjeta,
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
}

/** Píldora que dice de dónde vienen los datos; al tocarla se abre la configuración del servidor. */
@Composable
private fun EstadoConexion(origen: Origen, onClick: () -> Unit) {
    val (texto, color) = when (origen) {
        Origen.Servidor -> "Conectado a la base de datos (XAMPP)" to Vitalis.Teal
        Origen.Conectando -> "Conectando con el servidor…" to Vitalis.Ambar
        Origen.Ejemplo -> "Sin servidor · datos de ejemplo" to Vitalis.Ambar
    }
    Row(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.14f))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(8.dp))
        Text(texto, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun Resumen(citas: List<Cita>, modifier: Modifier) {
    val datos = listOf(
        Estado.Confirmada to "Confirmadas",
        Estado.PorConfirmar to "Por confirmar",
        Estado.Atendida to "Atendidas",
    )
    Row(
        modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(colorTarjeta)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.large)
            .padding(vertical = 12.dp),
    ) {
        datos.forEachIndexed { i, (estadoCita, etiqueta) ->
            if (i > 0) Box(Modifier.width(1.dp).height(36.dp).background(MaterialTheme.colorScheme.outlineVariant).align(Alignment.CenterVertically))
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${citas.count { it.estado == estadoCita }}", style = MaterialTheme.typography.headlineSmall, color = colorEstado(estadoCita).second)
                Text(etiqueta, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun FilaCita(cita: Cita, seleccionada: Boolean, onClick: () -> Unit, modifier: Modifier) {
    val interaccion = rememberInteraccion()
    val borde by animateColorAsState(
        if (seleccionada) Vitalis.Teal else MaterialTheme.colorScheme.outlineVariant,
        animationSpec = resorte(),
        label = "borde",
    )
    val color = cita.especialidad.color
    val pasada = !cita.estado.esProxima
    Row(
        modifier
            .fillMaxWidth()
            .presionable(interaccion)
            .clip(MaterialTheme.shapes.large)
            .background(colorTarjeta)
            .border(if (seleccionada) 2.dp else 1.dp, borde, MaterialTheme.shapes.large)
            .clickable(interactionSource = interaccion, indication = ripple(), onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            Modifier
                .width(58.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (pasada) MaterialTheme.colorScheme.surfaceVariant else color.copy(alpha = if (esOscuro) 0.2f else 0.12f))
                .padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val tinta = if (pasada) MaterialTheme.colorScheme.onSurfaceVariant else color
            Text(cita.fecha.diaSemana.uppercase(), style = MaterialTheme.typography.labelSmall, color = tinta)
            Text("${cita.fecha.dia}", style = MaterialTheme.typography.headlineMedium, color = if (pasada) tinta else MaterialTheme.colorScheme.onSurface)
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
