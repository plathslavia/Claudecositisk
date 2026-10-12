package com.vitalis.eps

import androidx.compose.runtime.Composable

// En escritorio no hay botón "Atrás" del sistema ni barra de estado: se navega con las flechas de la app.

@Composable
actual fun BotonAtras(enabled: Boolean, onBack: () -> Unit) = Unit

@Composable
actual fun BarraDeEstadoClara(clara: Boolean) = Unit
