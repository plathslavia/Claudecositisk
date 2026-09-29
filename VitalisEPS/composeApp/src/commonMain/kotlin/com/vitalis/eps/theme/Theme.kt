package com.vitalis.eps.theme

import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.vitalis.eps.resources.Res
import com.vitalis.eps.resources.plusjakartasans_400
import com.vitalis.eps.resources.plusjakartasans_500
import com.vitalis.eps.resources.plusjakartasans_600
import com.vitalis.eps.resources.plusjakartasans_700
import com.vitalis.eps.resources.sora_600
import com.vitalis.eps.resources.sora_700
import com.vitalis.eps.resources.sora_800
import org.jetbrains.compose.resources.Font

/** Paleta de la marca: azul noche de fondo, teal eléctrico como acento principal y violeta de apoyo. */
object Vitalis {
    val Noche = Color(0xFF0B1B3A)
    val Indigo = Color(0xFF1E2A78)
    val Violeta = Color(0xFF6C5CE7)
    val VioletaClaro = Color(0xFFECE9FF)
    val Teal = Color(0xFF12D6B0)
    val TealOscuro = Color(0xFF0A8F79)
    val TealClaro = Color(0xFFDDF8F2)
    val Coral = Color(0xFFF2545B)
    val CoralClaro = Color(0xFFFDE7E8)
    val Ambar = Color(0xFFF5A524)
    val AmbarClaro = Color(0xFFFEF3DC)
    val Fondo = Color(0xFFF3F6FB)
    val Tinta = Color(0xFF0F172A)
    val TintaSuave = Color(0xFF5B6478)
    val Linea = Color(0xFFE3E8F1)
}

private val colores = lightColorScheme(
    primary = Vitalis.Noche,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE6ECF8),
    onPrimaryContainer = Vitalis.Noche,
    secondary = Vitalis.Teal,
    onSecondary = Vitalis.Noche,
    secondaryContainer = Vitalis.TealClaro,
    onSecondaryContainer = Color(0xFF064E42),
    tertiary = Vitalis.Violeta,
    onTertiary = Color.White,
    tertiaryContainer = Vitalis.VioletaClaro,
    onTertiaryContainer = Color(0xFF2B2170),
    error = Vitalis.Coral,
    background = Vitalis.Fondo,
    onBackground = Vitalis.Tinta,
    surface = Color.White,
    onSurface = Vitalis.Tinta,
    surfaceVariant = Color(0xFFEAEFF7),
    onSurfaceVariant = Vitalis.TintaSuave,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF8FAFD),
    surfaceContainer = Color(0xFFF0F4FA),
    surfaceContainerHigh = Color(0xFFEAEFF7),
    outline = Color(0xFFB7C0D1),
    outlineVariant = Vitalis.Linea,
)

@Composable
private fun familiaTitulos() = FontFamily(
    Font(Res.font.sora_600, FontWeight.SemiBold),
    Font(Res.font.sora_700, FontWeight.Bold),
    Font(Res.font.sora_800, FontWeight.ExtraBold),
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
        displayMedium = TextStyle(fontFamily = titulos, fontWeight = FontWeight.ExtraBold, fontSize = 44.sp, lineHeight = 46.sp, letterSpacing = (-0.035).em),
        displaySmall = TextStyle(fontFamily = titulos, fontWeight = FontWeight.ExtraBold, fontSize = 34.sp, lineHeight = 38.sp, letterSpacing = (-0.03).em),
        headlineMedium = TextStyle(fontFamily = titulos, fontWeight = FontWeight.Bold, fontSize = 27.sp, lineHeight = 32.sp, letterSpacing = (-0.025).em),
        headlineSmall = TextStyle(fontFamily = titulos, fontWeight = FontWeight.Bold, fontSize = 21.sp, lineHeight = 27.sp, letterSpacing = (-0.02).em),
        titleLarge = TextStyle(fontFamily = titulos, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 24.sp, letterSpacing = (-0.01).em),
        titleMedium = TextStyle(fontFamily = texto, fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 22.sp),
        titleSmall = TextStyle(fontFamily = texto, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
        bodyLarge = TextStyle(fontFamily = texto, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 25.sp),
        bodyMedium = TextStyle(fontFamily = texto, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 21.sp),
        bodySmall = TextStyle(fontFamily = texto, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 17.sp, letterSpacing = 0.01.em),
        labelLarge = TextStyle(fontFamily = texto, fontWeight = FontWeight.Bold, fontSize = 15.sp, lineHeight = 20.sp),
        labelMedium = TextStyle(fontFamily = texto, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.02.em),
        labelSmall = TextStyle(fontFamily = texto, fontWeight = FontWeight.Bold, fontSize = 11.sp, lineHeight = 14.sp, letterSpacing = 0.09.em),
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
fun VitalisTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = colores,
        typography = tipografia(),
        shapes = formas,
        content = content,
    )
}
