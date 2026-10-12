package com.vitalis.eps.ui

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.EventAvailable
import androidx.compose.material.icons.rounded.EventBusy
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vitalis.eps.data.Cita
import com.vitalis.eps.data.Estado
import com.vitalis.eps.data.Profesional
import com.vitalis.eps.data.iniciales
import com.vitalis.eps.theme.Vitalis
import com.vitalis.eps.theme.colorTarjeta
import kotlinx.coroutines.launch

private enum class Pestana { Citas, Profesionales }

/**
 * Panel de administración: todas las citas de todos los afiliados y los profesionales de la red.
 * Desde aquí se crean citas y profesionales, y se cambia el estado o se elimina cualquier cita.
 * Tocar una cita abre la misma vista Detalle que usa el afiliado.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PantallaAdmin(
    estado: EstadoAgenda,
    seleccionadaId: String?,
    aviso: String?,
    onAvisoMostrado: () -> Unit,
    onVolver: () -> Unit,
    onAbrirCita: (Cita) -> Unit,
    onNuevaCita: () -> Unit,
    onCambiarEstado: (String, Estado) -> Unit,
    onCambiarTema: () -> Unit,
    onCerrarSesion: () -> Unit,
    onReintentar: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var pestana by rememberSaveable { mutableStateOf(Pestana.Citas) }
    var nuevoProfesional by remember { mutableStateOf(false) }
    var porEliminar by remember { mutableStateOf<Cita?>(null) }
    var dialogoServidor by remember { mutableStateOf(false) }
    val avisos = remember { SnackbarHostState() }
    val alcance = rememberCoroutineScope()

    LaunchedEffect(aviso) {
        if (aviso != null) {
            avisos.showSnackbar(aviso)
            onAvisoMostrado()
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0),
        snackbarHost = { SnackbarHost(avisos, Modifier.navigationBarsPadding()) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { if (pestana == Pestana.Citas) onNuevaCita() else nuevoProfesional = true },
                icon = { Icon(Icons.Rounded.Add, contentDescription = null) },
                text = { Text(if (pestana == Pestana.Citas) "Nueva cita" else "Nuevo profesional", style = MaterialTheme.typography.labelLarge) },
                containerColor = Vitalis.Acento,
                contentColor = Vitalis.SobreAcento,
                modifier = Modifier.navigationBarsPadding(),
            )
        },
    ) { _ ->
        Column(Modifier.fillMaxSize()) {
            Spacer(Modifier.statusBarsPadding())
            LazyColumn(
                state = estado.listaAdmin,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 110.dp),
            ) {
                item(key = "encabezado") {
                    Column(Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onVolver) {
                                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver al inicio")
                            }
                            Spacer(Modifier.weight(1f))
                            BotonTema(onCambiarTema)
                            IconButton(onClick = onCerrarSesion) {
                                Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = "Cerrar sesión")
                            }
                        }
                        Column(Modifier.padding(start = 8.dp, top = 4.dp, end = 8.dp)) {
                            Text("Panel de administración", style = MaterialTheme.typography.headlineMedium)
                            Text(
                                estado.sesion?.nombre.orEmpty(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Spacer(Modifier.height(10.dp))
                            EstadoConexion(estado.origen, onClick = { dialogoServidor = true })
                        }
                    }
                }
                item(key = "cifras") { CifrasAdmin(estado, Modifier.padding(horizontal = 20.dp).padding(top = 16.dp)) }
                stickyHeader(key = "pestanas") {
                    Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background).padding(top = 14.dp, bottom = 6.dp)) {
                        Row(Modifier.padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ChipPestana("Citas · ${estado.citas.size}", pestana == Pestana.Citas) { pestana = Pestana.Citas }
                            ChipPestana("Profesionales · ${estado.profesionales.size}", pestana == Pestana.Profesionales) { pestana = Pestana.Profesionales }
                        }
                        if (pestana == Pestana.Citas) FiltrosCitas(estado)
                    }
                }
                if (pestana == Pestana.Citas) {
                    item(key = "buscador") { Buscador(estado) }
                    val lista = estado.resultados
                    if (lista.isEmpty()) {
                        item(key = "vacio") { Vacio("No hay citas con ese filtro", "Prueba con otro filtro o crea una cita nueva.") }
                    }
                    items(lista, key = { it.id }) { cita ->
                        FilaCitaAdmin(
                            cita = cita,
                            seleccionada = cita.id == seleccionadaId,
                            onClick = { onAbrirCita(cita) },
                            onCambiarEstado = { onCambiarEstado(cita.id, it) },
                            onEliminar = { porEliminar = cita },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp),
                        )
                    }
                } else {
                    val grupos = estado.profesionales.groupBy { it.especialidad }.toList().sortedBy { it.first.nombre }
                    grupos.forEach { (especialidad, lista) ->
                        item(key = "esp-${especialidad.name}") {
                            Text(
                                especialidad.nombre.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 6.dp),
                            )
                        }
                        items(lista, key = { "prof-${it.id}" }) { p ->
                            FilaProfesional(
                                p,
                                proximas = estado.citas.count { it.profesional == p.nombre && it.estado.esProxima },
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                            )
                        }
                    }
                }
            }
        }
    }

    if (nuevoProfesional) {
        DialogoProfesional(
            onGuardar = { nombre, registro, especialidad -> estado.crearProfesional(nombre, registro, especialidad) },
            onListo = { nombre ->
                nuevoProfesional = false
                alcance.launch { avisos.showSnackbar("Se agregó a $nombre") }
            },
            onCerrar = { nuevoProfesional = false },
        )
    }
    porEliminar?.let { cita ->
        AlertDialog(
            onDismissRequest = { porEliminar = null },
            icon = { Icon(Icons.Rounded.DeleteOutline, contentDescription = null, tint = Vitalis.Peligro) },
            title = { Text("¿Eliminar la cita?") },
            text = {
                Text(
                    "${cita.especialidad.nombre} de ${cita.paciente}, ${cita.fecha.corta} a las ${cita.hora}. Esta acción no se puede deshacer.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    porEliminar = null
                    alcance.launch {
                        val error = estado.eliminarCita(cita.id)
                        avisos.showSnackbar(error ?: "Cita eliminada")
                    }
                }) { Text("Eliminar", color = Vitalis.Peligro) }
            },
            dismissButton = { TextButton(onClick = { porEliminar = null }) { Text("Cancelar") } },
        )
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
private fun CifrasAdmin(estado: EstadoAgenda, modifier: Modifier) {
    val citas = estado.citas
    val datos = listOf(
        Triple("${citas.count { it.estado.esProxima }}", "Próximas", Vitalis.AcentoOscuro),
        Triple("${citas.count { it.estado == Estado.PorConfirmar }}", "Pendientes", colorEstado(Estado.PorConfirmar).second),
        Triple("${estado.afiliados.size}", "Pacientes", Vitalis.Acento2),
        Triple("${estado.profesionales.size}", "Médicos", MaterialTheme.colorScheme.onSurface),
    )
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        datos.forEach { (valor, etiqueta, color) ->
            Column(
                Modifier
                    .weight(1f)
                    .clip(MaterialTheme.shapes.medium)
                    .background(colorTarjeta)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.medium)
                    .padding(vertical = 12.dp, horizontal = 10.dp),
            ) {
                Text(valor, style = MaterialTheme.typography.headlineSmall, color = color)
                Text(etiqueta, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            }
        }
    }
}

@Composable
private fun ChipPestana(texto: String, activa: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = activa,
        onClick = onClick,
        label = { Text(texto, style = MaterialTheme.typography.labelLarge) },
        shape = RoundedCornerShape(14.dp),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = colorTarjeta,
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = activa,
            borderColor = MaterialTheme.colorScheme.outlineVariant,
            selectedBorderColor = MaterialTheme.colorScheme.primary,
        ),
    )
}

@Composable
private fun FiltrosCitas(estado: EstadoAgenda) {
    LazyRow(
        Modifier.padding(top = 8.dp),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(Filtro.entries) { opcion ->
            val activo = estado.filtro == opcion
            FilterChip(
                selected = activo,
                onClick = { estado.filtro = opcion },
                label = { Text(opcion.etiqueta) },
                shape = RoundedCornerShape(50),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = colorTarjeta,
                    selectedContainerColor = Vitalis.Acento.copy(alpha = 0.22f),
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = activo,
                    borderColor = MaterialTheme.colorScheme.outlineVariant,
                    selectedBorderColor = Vitalis.Acento,
                ),
            )
        }
    }
}

@Composable
private fun Buscador(estado: EstadoAgenda) {
    OutlinedTextField(
        value = estado.busqueda,
        onValueChange = { estado.busqueda = it },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(top = 4.dp, bottom = 8.dp),
        placeholder = { Text("Buscar paciente o médico") },
        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
        trailingIcon = {
            if (estado.busqueda.isNotEmpty()) {
                IconButton(onClick = { estado.busqueda = "" }) { Icon(Icons.Rounded.Close, contentDescription = "Borrar búsqueda") }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(18.dp),
        textStyle = MaterialTheme.typography.bodyMedium,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = colorTarjeta,
            focusedContainerColor = colorTarjeta,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedBorderColor = Vitalis.Acento,
        ),
    )
}

@Composable
private fun FilaCitaAdmin(
    cita: Cita,
    seleccionada: Boolean,
    onClick: () -> Unit,
    onCambiarEstado: (Estado) -> Unit,
    onEliminar: () -> Unit,
    modifier: Modifier,
) {
    var menu by remember { mutableStateOf(false) }
    val interaccion = rememberInteraccion()
    val color = cita.especialidad.color
    Row(
        modifier
            .fillMaxWidth()
            .presionable(interaccion)
            .clip(MaterialTheme.shapes.large)
            .background(colorTarjeta)
            .border(if (seleccionada) 2.dp else 1.dp, if (seleccionada) Vitalis.Acento else MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.large)
            .clickable(interactionSource = interaccion, indication = ripple(), onClick = onClick)
            .padding(start = 12.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            Modifier.width(52.dp).clip(RoundedCornerShape(14.dp)).background(color.copy(alpha = 0.13f)).padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(cita.fecha.diaSemana.uppercase(), style = MaterialTheme.typography.labelSmall, color = color)
            Text("${cita.fecha.dia}", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
            Text(cita.fecha.mesCorto, style = MaterialTheme.typography.bodySmall, color = color)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(cita.paciente, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconoEspecialidad(cita.especialidad, tamano = 18.dp)
                Spacer(Modifier.width(6.dp))
                Text(
                    "${cita.especialidad.nombre} · ${cita.profesional}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                "${cita.hora} · ${cita.sede}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
            Spacer(Modifier.height(6.dp))
            ChipEstado(cita.estado)
        }
        Box {
            IconButton(onClick = { menu = true }) { Icon(Icons.Rounded.MoreVert, contentDescription = "Acciones de la cita") }
            DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                if (cita.estado != Estado.Confirmada) {
                    OpcionMenu(Icons.Rounded.EventAvailable, "Confirmar") { menu = false; onCambiarEstado(Estado.Confirmada) }
                }
                if (cita.estado != Estado.Atendida) {
                    OpcionMenu(Icons.Rounded.TaskAlt, "Marcar como atendida") { menu = false; onCambiarEstado(Estado.Atendida) }
                }
                if (cita.estado != Estado.Cancelada) {
                    OpcionMenu(Icons.Rounded.EventBusy, "Cancelar") { menu = false; onCambiarEstado(Estado.Cancelada) }
                }
                OpcionMenu(Icons.Rounded.DeleteOutline, "Eliminar", Vitalis.Peligro) { menu = false; onEliminar() }
            }
        }
    }
}

@Composable
private fun OpcionMenu(icono: ImageVector, texto: String, color: Color = MaterialTheme.colorScheme.onSurface, onClick: () -> Unit) {
    DropdownMenuItem(
        text = { Text(texto, color = color) },
        leadingIcon = { Icon(icono, contentDescription = null, tint = color) },
        onClick = onClick,
    )
}

@Composable
private fun FilaProfesional(p: Profesional, proximas: Int, modifier: Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(colorTarjeta)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.large)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Avatar(iniciales(p.nombre).ifEmpty { "?" }, p.especialidad.color, tamano = 42.dp)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(p.nombre, style = MaterialTheme.typography.titleSmall)
            Text("${p.especialidad.nombre} · ${p.registro}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (proximas > 0) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = colorEstado(Estado.Confirmada).second, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("$proximas próximas", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun Vacio(titulo: String, texto: String) {
    Column(Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Rounded.EventBusy, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(44.dp))
        Spacer(Modifier.height(10.dp))
        Text(titulo, style = MaterialTheme.typography.titleSmall)
        Text(texto, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
