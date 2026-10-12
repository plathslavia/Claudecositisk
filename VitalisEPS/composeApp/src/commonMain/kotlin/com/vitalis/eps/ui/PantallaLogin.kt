package com.vitalis.eps.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.vitalis.eps.data.Empresa
import com.vitalis.eps.theme.Vitalis
import com.vitalis.eps.theme.colorTarjeta
import kotlinx.coroutines.launch

/**
 * Inicio de sesión. El afiliado entra con su documento y el administrador con el usuario "admin".
 * Las cuentas de prueba se llenan con un toque para que la demostración sea rápida.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PantallaLogin(
    estado: EstadoAgenda,
    onVolver: () -> Unit,
    onIngresar: () -> Unit,
    onReintentar: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var usuario by rememberSaveable { mutableStateOf("") }
    var clave by rememberSaveable { mutableStateOf("") }
    var verClave by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var cargando by remember { mutableStateOf(false) }
    var dialogoServidor by remember { mutableStateOf(false) }
    val alcance = rememberCoroutineScope()

    fun ingresar() {
        if (cargando) return
        cargando = true
        error = null
        alcance.launch {
            val problema = estado.iniciarSesion(usuario, clave)
            cargando = false
            if (problema == null) onIngresar() else error = problema
        }
    }

    Box(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Banda superior con el fondo de la marca.
        Box(
            Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                .fondoMalla(Vitalis),
        )
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .imePadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onVolver,
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White.copy(alpha = 0.14f), contentColor = Color.White),
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver al inicio")
                }
            }
            Spacer(Modifier.height(8.dp))
            LogoVitalis(Modifier.size(64.dp))
            Spacer(Modifier.height(14.dp))
            Text("Bienvenido a ${Empresa.NOMBRE}", style = MaterialTheme.typography.headlineMedium, color = Color.White)
            Text(
                "Ingresa para ver y gestionar tus citas",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.75f),
            )
            Spacer(Modifier.height(28.dp))

            Column(
                Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(colorTarjeta)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(28.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                EstadoServidor(estado.origen, onClick = { dialogoServidor = true })
                OutlinedTextField(
                    value = usuario,
                    onValueChange = { usuario = it; error = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Documento o usuario") },
                    leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                    colors = coloresCampo(),
                )
                OutlinedTextField(
                    value = clave,
                    onValueChange = { clave = it; error = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { verClave = !verClave }) {
                            Icon(
                                if (verClave) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                contentDescription = if (verClave) "Ocultar contraseña" else "Mostrar contraseña",
                            )
                        }
                    },
                    visualTransformation = if (verClave) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { ingresar() }),
                    colors = coloresCampo(),
                )
                AnimatedVisibility(visible = error != null, enter = fadeIn(), exit = fadeOut()) {
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Vitalis.Peligro.copy(alpha = 0.12f)).padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Rounded.ErrorOutline, contentDescription = null, tint = Vitalis.Peligro, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text(error.orEmpty(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
                val interaccion = rememberInteraccion()
                Button(
                    onClick = ::ingresar,
                    enabled = !cargando,
                    interactionSource = interaccion,
                    modifier = Modifier.fillMaxWidth().height(54.dp).presionable(interaccion),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Vitalis.Acento, contentColor = Vitalis.SobreAcento),
                ) {
                    if (cargando) {
                        CircularProgressIndicator(Modifier.size(22.dp), color = Vitalis.SobreAcento, strokeWidth = 2.5.dp)
                    } else {
                        Icon(Icons.AutoMirrored.Rounded.Login, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("Ingresar", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            Spacer(Modifier.height(22.dp))
            Column(Modifier.widthIn(max = 480.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("CUENTAS DE PRUEBA", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CuentaPrueba(Icons.Rounded.Badge, "Afiliado · Camilo") { usuario = "1020456789"; clave = "camilo123"; error = null }
                    CuentaPrueba(Icons.Rounded.Badge, "Afiliado · Andrés") { usuario = "1018223344"; clave = "andres123"; error = null }
                    CuentaPrueba(Icons.Rounded.AdminPanelSettings, "Administrador") { usuario = "admin"; clave = "admin123"; error = null }
                }
                Text(
                    "Toca una cuenta para llenar los datos y luego pulsa Ingresar.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.navigationBarsPadding().height(24.dp))
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
private fun coloresCampo() = OutlinedTextFieldDefaults.colors(
    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
    focusedBorderColor = Vitalis.Acento,
    focusedLeadingIconColor = Vitalis.Acento,
)

/** Píldora que dice si el login irá al servidor o al modo de demostración. */
@Composable
private fun EstadoServidor(origen: Origen, onClick: () -> Unit) {
    val (texto, color) = when (origen) {
        Origen.Servidor -> "Servidor XAMPP conectado" to Vitalis.Acento
        Origen.Conectando -> "Buscando el servidor…" to Vitalis.Aviso
        Origen.Ejemplo -> "Sin servidor · modo demostración" to Vitalis.Aviso
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
private fun CuentaPrueba(icono: ImageVector, texto: String, onClick: () -> Unit) {
    Row(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(colorTarjeta)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icono, contentDescription = null, tint = Vitalis.AcentoOscuro, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(texto, style = MaterialTheme.typography.labelMedium)
    }
}
