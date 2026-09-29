package com.cumbrecafe.app

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
actual fun BotonAtras(enabled: Boolean, onBack: () -> Unit) {
    BackHandler(enabled = enabled, onBack = onBack)
}

@Composable
actual fun BarraDeEstadoClara(clara: Boolean) {
    val vista = LocalView.current
    if (vista.isInEditMode) return
    SideEffect {
        val ventana = (vista.context as? Activity)?.window ?: return@SideEffect
        // "clara" = íconos blancos sobre fondo oscuro, así que la apariencia NO es de barra clara.
        WindowCompat.getInsetsController(ventana, vista).isAppearanceLightStatusBars = !clara
    }
}
