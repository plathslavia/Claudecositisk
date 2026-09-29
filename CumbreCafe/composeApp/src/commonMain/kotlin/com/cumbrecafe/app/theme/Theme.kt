package com.cumbrecafe.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.cumbrecafe.app.resources.Res
import com.cumbrecafe.app.resources.fraunces_600
import com.cumbrecafe.app.resources.fraunces_700
import com.cumbrecafe.app.resources.plusjakartasans_400
import com.cumbrecafe.app.resources.plusjakartasans_500
import com.cumbrecafe.app.resources.plusjakartasans_600
import com.cumbrecafe.app.resources.plusjakartasans_700
import org.jetbrains.compose.resources.Font

/** Paleta de la marca: tonos de café tostado, crema y caramelo, con un verde de montaña como acento. */
object Cumbre {
    val Espresso = Color(0xFF24160F)
    val Tostado = Color(0xFF3B2519)
    val Cacao = Color(0xFF6B4431)
    val Caramelo = Color(0xFFC8894B)
    val CarameloClaro = Color(0xFFE9B97F)
    val Crema = Color(0xFFF7F1E8)
    val Papel = Color(0xFFFFFBF5)
    val Arena = Color(0xFFEDE3D5)
    val Montana = Color(0xFF4F6B4C)
    val MontanaClaro = Color(0xFFDDE7D6)
    val Tinta = Color(0xFF1F1712)
    val TintaSuave = Color(0xFF6F6259)
    val Linea = Color(0xFFE4D8C8)
}

private val colores = lightColorScheme(
    primary = Cumbre.Tostado,
    onPrimary = Cumbre.Crema,
    primaryContainer = Cumbre.Arena,
    onPrimaryContainer = Cumbre.Tostado,
    secondary = Cumbre.Caramelo,
    onSecondary = Cumbre.Espresso,
    secondaryContainer = Color(0xFFF6E3CC),
    onSecondaryContainer = Cumbre.Tostado,
    tertiary = Cumbre.Montana,
    onTertiary = Color.White,
    tertiaryContainer = Cumbre.MontanaClaro,
    onTertiaryContainer = Color(0xFF22331F),
    background = Cumbre.Crema,
    onBackground = Cumbre.Tinta,
    surface = Cumbre.Papel,
    onSurface = Cumbre.Tinta,
    surfaceVariant = Cumbre.Arena,
    onSurfaceVariant = Cumbre.TintaSuave,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Cumbre.Papel,
    surfaceContainer = Color(0xFFF9F3EA),
    surfaceContainerHigh = Cumbre.Arena,
    outline = Color(0xFFBFAF9C),
    outlineVariant = Cumbre.Linea,
)

@Composable
private fun familiaTitulos() = FontFamily(
    Font(Res.font.fraunces_600, FontWeight.SemiBold),
    Font(Res.font.fraunces_700, FontWeight.Bold),
)

@Composable
private fun familiaTexto() = FontFamily(
    Font(Res.font.plusjakartasans_400, FontWeight.Normal),
    Font(Res.font.plusjakartasans_500, FontWeight.Medium),
    Font(Res.font.plusjakartasans_600, FontWeight.SemiBold),
    Font(Res.font.plusjakartasans_700, FontWeight.Bold),
)

/**
 * Tipografía: títulos grandes con interletrado negativo e interlineado ajustado;
 * texto pequeño con interletrado levemente positivo para que se lea mejor.
 */
@Composable
private fun tipografia(): Typography {
    val titulos = familiaTitulos()
    val texto = familiaTexto()
    return Typography(
        displaySmall = TextStyle(fontFamily = titulos, fontWeight = FontWeight.Bold, fontSize = 34.sp, lineHeight = 38.sp, letterSpacing = (-0.02).em),
        headlineMedium = TextStyle(fontFamily = titulos, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 32.sp, letterSpacing = (-0.015).em),
        headlineSmall = TextStyle(fontFamily = titulos, fontWeight = FontWeight.SemiBold, fontSize = 23.sp, lineHeight = 28.sp, letterSpacing = (-0.01).em),
        titleLarge = TextStyle(fontFamily = titulos, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 25.sp, letterSpacing = (-0.005).em),
        titleMedium = TextStyle(fontFamily = texto, fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 22.sp),
        titleSmall = TextStyle(fontFamily = texto, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
        bodyLarge = TextStyle(fontFamily = texto, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 25.sp),
        bodyMedium = TextStyle(fontFamily = texto, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 21.sp),
        bodySmall = TextStyle(fontFamily = texto, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 17.sp, letterSpacing = 0.01.em),
        labelLarge = TextStyle(fontFamily = texto, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, lineHeight = 20.sp),
        labelMedium = TextStyle(fontFamily = texto, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.02.em),
        labelSmall = TextStyle(fontFamily = texto, fontWeight = FontWeight.Bold, fontSize = 11.sp, lineHeight = 14.sp, letterSpacing = 0.08.em),
    )
}

private val formas = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

@Composable
fun CumbreTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = colores,
        typography = tipografia(),
        shapes = formas,
        content = content,
    )
}
