package com.vitalis.eps.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.vitalis.eps.data.Especialidad
import com.vitalis.eps.data.Modalidad
import com.vitalis.eps.data.NuevaCita
import com.vitalis.eps.data.fechaDesdeIso
import com.vitalis.eps.data.horaLegible
import com.vitalis.eps.data.pesos
import com.vitalis.eps.theme.Vitalis
import com.vitalis.eps.theme.colorTarjeta
import kotlinx.coroutines.launch

/**
 * Formulario del administrador para crear una cita. Al guardar, la cita queda "Por confirmar"
 * y el afiliado la ve en su lista. [onCreada] recibe el mensaje para mostrar en el panel.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PantallaNuevaCita(estado: EstadoAgenda, onVolver: () -> Unit, onCreada: (String) -> Unit, modifier: Modifier = Modifier) {
    var idAfiliado by rememberSaveable { mutableStateOf<Int?>(null) }
    var idProfesional by rememberSaveable { mutableStateOf<Int?>(null) }
    var modalidad by rememberSaveable { mutableStateOf(Modalidad.Presencial) }
    var idSede by rememberSaveable { mutableStateOf<Int?>(null) }
    var fecha by rememberSaveable { mutableStateOf("") }
    var hora by rememberSaveable { mutableStateOf("") }
    var duracion by rememberSaveable { mutableStateOf(20) }
    var cuota by rememberSaveable { mutableStateOf(5200) }
    var motivo by rememberSaveable { mutableStateOf("") }
    var preparacion by rememberSaveable { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var guardando by remember { mutableStateOf(false) }
    var eligiendoFecha by remember { mutableStateOf(false) }
    var eligiendoHora by remember { mutableStateOf(false) }
    val alcance = rememberCoroutineScope()

    val sedesPresenciales = estado.sedes.filter { it.direccion.isNotBlank() }
    val sedeVirtual = estado.sedes.firstOrNull { it.direccion.isBlank() }

    fun guardar() {
        val sede = if (modalidad == Modalidad.Telemedicina) sedeVirtual?.id else idSede
        error = when {
            idAfiliado == null -> "Elige el paciente."
            idProfesional == null -> "Elige el profesional."
            sede == null -> "Elige la sede."
            fecha.isEmpty() -> "Elige la fecha."
            hora.isEmpty() -> "Elige la hora."
            motivo.isBlank() -> "Escribe el motivo de la consulta."
            else -> null
        }
        if (error != null || guardando) return
        guardando = true
        val nueva = NuevaCita(
            idAfiliado = idAfiliado!!,
            idProfesional = idProfesional!!,
            idSede = sede!!,
            fecha = fecha,
            hora = hora,
            duracionMin = duracion,
            modalidad = modalidad,
            motivo = motivo.trim(),
            cuotaModeradora = cuota,
            preparacion = preparacion.lines().map { it.trim() }.filter { it.isNotEmpty() },
        )
        alcance.launch {
            val problema = estado.crearCita(nueva)
            guardando = false
            if (problema == null) {
                onCreada("Cita creada para ${estado.afiliado(nueva.idAfiliado)?.nombre ?: "el paciente"}")
            } else {
                error = problema
            }
        }
    }

    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Row(Modifier.statusBarsPadding().padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onVolver) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver al panel") }
            Spacer(Modifier.width(4.dp))
            Column {
                Text("Nueva cita", style = MaterialTheme.typography.headlineSmall)
                Text("Quedará por confirmar hasta que el paciente la confirme", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(Modifier.widthIn(max = 640.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Spacer(Modifier.height(4.dp))
                Selector(
                    etiqueta = "Paciente",
                    opciones = estado.afiliados.map { it.id to "${it.nombre} · CC ${it.documento}" },
                    elegida = idAfiliado,
                    onElegir = { idAfiliado = it; error = null },
                )
                Selector(
                    etiqueta = "Profesional",
                    opciones = estado.profesionales.sortedBy { it.especialidad.nombre }.map { it.id to "${it.nombre} · ${it.especialidad.nombre}" },
                    elegida = idProfesional,
                    onElegir = { idProfesional = it; error = null },
                )
                Campo("Modalidad") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Modalidad.entries.forEach { m ->
                            OpcionChip(m.etiqueta, modalidad == m) { modalidad = m }
                        }
                    }
                }
                AnimatedVisibility(visible = modalidad == Modalidad.Presencial) {
                    Selector(
                        etiqueta = "Sede",
                        opciones = sedesPresenciales.map { it.id to "${it.nombre} · ${it.direccion}" },
                        elegida = idSede,
                        onElegir = { idSede = it; error = null },
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CampoBoton(
                        etiqueta = "Fecha",
                        valor = if (fecha.isEmpty()) "" else fechaDesdeIso(fecha).larga,
                        icono = Icons.Rounded.CalendarMonth,
                        onClick = { eligiendoFecha = true },
                        modifier = Modifier.weight(1.4f),
                    )
                    CampoBoton(
                        etiqueta = "Hora",
                        valor = if (hora.isEmpty()) "" else horaLegible(hora),
                        icono = Icons.Rounded.Schedule,
                        onClick = { eligiendoHora = true },
                        modifier = Modifier.weight(1f),
                    )
                }
                Campo("Duración") {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(15, 20, 30, 45, 60).forEach { m -> OpcionChip("$m min", duracion == m) { duracion = m } }
                    }
                }
                Campo("Cuota moderadora") {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(0, 5200, 20600).forEach { v -> OpcionChip(pesos(v), cuota == v) { cuota = v } }
                    }
                }
                OutlinedTextField(
                    value = motivo,
                    onValueChange = { if (it.length <= 255) motivo = it; error = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Motivo de la consulta") },
                    minLines = 2,
                    shape = RoundedCornerShape(16.dp),
                    colors = coloresCampoFormulario(),
                )
                OutlinedTextField(
                    value = preparacion,
                    onValueChange = { preparacion = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Preparación (un paso por línea, opcional)") },
                    placeholder = { Text("Ayuno de 8 horas\nTrae tu documento") },
                    minLines = 3,
                    shape = RoundedCornerShape(16.dp),
                    colors = coloresCampoFormulario(),
                )
                AnimatedVisibility(visible = error != null) {
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Vitalis.Peligro.copy(alpha = 0.12f)).padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Rounded.ErrorOutline, contentDescription = null, tint = Vitalis.Peligro, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text(error.orEmpty(), style = MaterialTheme.typography.bodySmall)
                    }
                }
                val interaccion = rememberInteraccion()
                Button(
                    onClick = ::guardar,
                    enabled = !guardando,
                    interactionSource = interaccion,
                    modifier = Modifier.fillMaxWidth().height(54.dp).presionable(interaccion),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Vitalis.Acento, contentColor = Vitalis.SobreAcento),
                ) {
                    if (guardando) {
                        CircularProgressIndicator(Modifier.size(22.dp), color = Vitalis.SobreAcento, strokeWidth = 2.5.dp)
                    } else {
                        Icon(Icons.Rounded.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("Guardar cita", style = MaterialTheme.typography.labelLarge)
                    }
                }
                Spacer(Modifier.navigationBarsPadding().height(24.dp))
            }
        }
    }

    if (eligiendoFecha) {
        val estadoFecha = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { eligiendoFecha = false },
            confirmButton = {
                TextButton(onClick = {
                    estadoFecha.selectedDateMillis?.let { fecha = isoDesdeMillis(it); error = null }
                    eligiendoFecha = false
                }) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { eligiendoFecha = false }) { Text("Cancelar") } },
        ) {
            DatePicker(estadoFecha)
        }
    }
    if (eligiendoHora) {
        val estadoHora = rememberTimePickerState(initialHour = 8, initialMinute = 0, is24Hour = false)
        AlertDialog(
            onDismissRequest = { eligiendoHora = false },
            title = { Text("Hora de la cita") },
            text = { TimePicker(estadoHora) },
            confirmButton = {
                TextButton(onClick = {
                    hora = "${estadoHora.hour.toString().padStart(2, '0')}:${estadoHora.minute.toString().padStart(2, '0')}"
                    error = null
                    eligiendoHora = false
                }) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { eligiendoHora = false }) { Text("Cancelar") } },
        )
    }
}

/** Diálogo para agregar un profesional a la red. [onGuardar] devuelve un mensaje de error o null. */
@Composable
fun DialogoProfesional(
    onGuardar: suspend (String, String, Especialidad) -> String?,
    onListo: (String) -> Unit,
    onCerrar: () -> Unit,
) {
    var nombre by remember { mutableStateOf("") }
    var registro by remember { mutableStateOf("") }
    var especialidad by remember { mutableStateOf<Int?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var guardando by remember { mutableStateOf(false) }
    val alcance = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onCerrar,
        icon = { Icon(Icons.Rounded.PersonAdd, contentDescription = null) },
        title = { Text("Nuevo profesional") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it.take(100); error = null },
                    label = { Text("Nombre (ej. Dra. Ana Ruiz)") },
                    singleLine = true,
                    colors = coloresCampoFormulario(),
                )
                OutlinedTextField(
                    value = registro,
                    onValueChange = { registro = it.take(30); error = null },
                    label = { Text("Registro (ej. RM 12.345)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    colors = coloresCampoFormulario(),
                )
                Selector(
                    etiqueta = "Especialidad",
                    opciones = Especialidad.entries.map { it.ordinal to it.nombre },
                    elegida = especialidad,
                    onElegir = { especialidad = it; error = null },
                )
                error?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = Vitalis.Peligro) }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !guardando,
                onClick = {
                    val e = especialidad?.let { Especialidad.entries[it] }
                    if (e == null) {
                        error = "Elige la especialidad."
                        return@TextButton
                    }
                    guardando = true
                    alcance.launch {
                        val problema = onGuardar(nombre, registro, e)
                        guardando = false
                        if (problema == null) onListo(nombre.trim()) else error = problema
                    }
                },
            ) { Text(if (guardando) "Guardando…" else "Guardar") }
        },
        dismissButton = { TextButton(onClick = onCerrar) { Text("Cancelar") } },
    )
}

/** Lista desplegable de opciones (id, texto). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Selector(etiqueta: String, opciones: List<Pair<Int, String>>, elegida: Int?, onElegir: (Int) -> Unit) {
    var abierto by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = abierto, onExpandedChange = { abierto = it }) {
        OutlinedTextField(
            value = opciones.firstOrNull { it.first == elegida }?.second.orEmpty(),
            onValueChange = {},
            readOnly = true,
            label = { Text(etiqueta) },
            placeholder = { Text("Elegir…") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = abierto) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = coloresCampoFormulario(),
            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
        )
        ExposedDropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            opciones.forEach { (id, texto) ->
                DropdownMenuItem(
                    text = { Text(texto, style = MaterialTheme.typography.bodyMedium) },
                    onClick = {
                        onElegir(id)
                        abierto = false
                    },
                )
            }
        }
    }
}

@Composable
private fun Campo(etiqueta: String, contenido: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(etiqueta, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        contenido()
    }
}

/** Campo de solo lectura que abre un selector (calendario o reloj) al tocarlo. */
@Composable
private fun CampoBoton(etiqueta: String, valor: String, icono: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit, modifier: Modifier) {
    Box(modifier) {
        OutlinedTextField(
            value = valor,
            onValueChange = {},
            readOnly = true,
            label = { Text(etiqueta) },
            placeholder = { Text("Elegir…") },
            trailingIcon = { Icon(icono, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = coloresCampoFormulario(),
            modifier = Modifier.fillMaxWidth(),
        )
        // Capa transparente encima para que todo el campo responda al toque.
        Box(Modifier.matchParentSize().clip(RoundedCornerShape(16.dp)).clickableSinEfecto(onClick))
    }
}

@Composable
private fun OpcionChip(texto: String, activo: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = activo,
        onClick = onClick,
        label = { Text(texto) },
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

@Composable
private fun coloresCampoFormulario() = OutlinedTextFieldDefaults.colors(
    unfocusedContainerColor = colorTarjeta,
    focusedContainerColor = colorTarjeta,
    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
    focusedBorderColor = Vitalis.Acento,
)

private fun Modifier.clickableSinEfecto(onClick: () -> Unit): Modifier =
    this.then(Modifier.clickable(interactionSource = null, indication = null, onClick = onClick))

/** Milisegundos UTC (lo que entrega el DatePicker) → "AAAA-MM-DD", sin depender de librerías de fechas. */
internal fun isoDesdeMillis(millis: Long): String {
    // Algoritmo "days from civil" de Howard Hinnant, al revés: días desde 1970-01-01 → año, mes y día.
    val z = millis.floorDiv(86_400_000L) + 719_468L
    val era = z.floorDiv(146_097L)
    val doe = z - era * 146_097L
    val yoe = (doe - doe / 1_460 + doe / 36_524 - doe / 146_096) / 365
    val doy = doe - (365 * yoe + yoe / 4 - yoe / 100)
    val mp = (5 * doy + 2) / 153
    val dia = doy - (153 * mp + 2) / 5 + 1
    val mes = if (mp < 10) mp + 3 else mp - 9
    val anio = yoe + era * 400 + if (mes <= 2) 1 else 0
    return "$anio-${mes.toString().padStart(2, '0')}-${dia.toString().padStart(2, '0')}"
}
