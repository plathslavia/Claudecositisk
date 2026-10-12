package com.vitalis.eps

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Vitalis EPS",
        // Tamaño de teléfono; al ensanchar la ventana, Maestro y Detalle se muestran lado a lado.
        state = rememberWindowState(width = 430.dp, height = 900.dp),
    ) {
        App()
    }
}
