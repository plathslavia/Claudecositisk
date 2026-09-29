package com.cumbrecafe.app

import androidx.compose.runtime.Composable

/** Botón o gesto "Atrás" del sistema. En Android usa el del teléfono; en escritorio no aplica. */
@Composable
expect fun BotonAtras(enabled: Boolean, onBack: () -> Unit)

/** Cambia el color de los íconos de la barra de estado según el fondo de la vista. */
@Composable
expect fun BarraDeEstadoClara(clara: Boolean)
