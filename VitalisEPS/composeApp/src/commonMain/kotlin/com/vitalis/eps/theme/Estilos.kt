package com.vitalis.eps.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Colores de marca de un estilo. Los nombres describen el papel del color, no el color en sí:
 * Base y Base2 pintan la portada y los encabezados (siempre oscuros o saturados, con texto blanco encima);
 * Acento es el color de los botones principales y SobreAcento el del texto que va sobre ellos.
 */
class Paleta(
    val Base: Color,
    val Base2: Color,
    val Acento: Color,
    val AcentoOscuro: Color,
    val AcentoClaro: Color,
    val SobreAcento: Color,
    val Acento2: Color,
    val Acento2Claro: Color,
    val Peligro: Color,
    val PeligroClaro: Color,
    val Aviso: Color,
    val AvisoClaro: Color,
    val Fondo: Color,
    val Tinta: Color,
    val TintaSuave: Color,
    val Linea: Color,
    val FondoOscuro: Color,
    val TarjetaOscura: Color,
    val LineaOscura: Color,
)

enum class FuenteTitulos { Sora, PlusJakarta, Fraunces, SpaceGrotesk }

enum class Estilo(val nombre: String, val descripcion: String, val titulos: FuenteTitulos, val paleta: Paleta) {
    Pulso(
        "Pulso",
        "Azul noche, teal eléctrico y violeta. Tecnológico y llamativo.",
        FuenteTitulos.Sora,
        Paleta(
            Base = Color(0xFF0B1B3A), Base2 = Color(0xFF1E2A78),
            Acento = Color(0xFF12D6B0), AcentoOscuro = Color(0xFF0A8F79), AcentoClaro = Color(0xFFDDF8F2), SobreAcento = Color(0xFF0B1B3A),
            Acento2 = Color(0xFF6C5CE7), Acento2Claro = Color(0xFFECE9FF),
            Peligro = Color(0xFFF2545B), PeligroClaro = Color(0xFFFDE7E8),
            Aviso = Color(0xFFF5A524), AvisoClaro = Color(0xFFFEF3DC),
            Fondo = Color(0xFFF3F6FB), Tinta = Color(0xFF0F172A), TintaSuave = Color(0xFF5B6478), Linea = Color(0xFFE3E8F1),
            FondoOscuro = Color(0xFF070D1C), TarjetaOscura = Color(0xFF111A30), LineaOscura = Color(0xFF223050),
        ),
    ),
    Calma(
        "Calma",
        "Azul clínico brillante y verde menta. Limpio, confiable y tranquilo.",
        FuenteTitulos.PlusJakarta,
        Paleta(
            Base = Color(0xFF1E4FD8), Base2 = Color(0xFF3D7BFF),
            Acento = Color(0xFF4ADE9B), AcentoOscuro = Color(0xFF0F9D63), AcentoClaro = Color(0xFFDCFBEA), SobreAcento = Color(0xFF06341F),
            Acento2 = Color(0xFF7CC4FF), Acento2Claro = Color(0xFFE3F1FF),
            Peligro = Color(0xFFE5484D), PeligroClaro = Color(0xFFFDE8E8),
            Aviso = Color(0xFFF2A93B), AvisoClaro = Color(0xFFFEF2DC),
            Fondo = Color(0xFFF6F8FC), Tinta = Color(0xFF0E1E3D), TintaSuave = Color(0xFF5A6782), Linea = Color(0xFFE3E9F3),
            FondoOscuro = Color(0xFF0A1120), TarjetaOscura = Color(0xFF121B2E), LineaOscura = Color(0xFF223050),
        ),
    ),
    Coral(
        "Coral",
        "Ciruela profunda, coral y crema, con títulos en serif. Cálido y cercano.",
        FuenteTitulos.Fraunces,
        Paleta(
            Base = Color(0xFF3A1D33), Base2 = Color(0xFF6B2D5C),
            Acento = Color(0xFFFF7A5C), AcentoOscuro = Color(0xFFD9482B), AcentoClaro = Color(0xFFFFE6DE), SobreAcento = Color(0xFF2A0F22),
            Acento2 = Color(0xFFFFB347), Acento2Claro = Color(0xFFFFF0D9),
            Peligro = Color(0xFFD92D4C), PeligroClaro = Color(0xFFFDE6EA),
            Aviso = Color(0xFFE39A2D), AvisoClaro = Color(0xFFFDF1DC),
            Fondo = Color(0xFFFBF6F1), Tinta = Color(0xFF2A1A24), TintaSuave = Color(0xFF6E5D66), Linea = Color(0xFFEEE3DA),
            FondoOscuro = Color(0xFF140A12), TarjetaOscura = Color(0xFF22141E), LineaOscura = Color(0xFF3A2433),
        ),
    ),
    Neon(
        "Neón",
        "Negro, verde lima y azul eléctrico. Urbano, nocturno y atrevido.",
        FuenteTitulos.SpaceGrotesk,
        Paleta(
            Base = Color(0xFF0A0A0A), Base2 = Color(0xFF1D1D27),
            Acento = Color(0xFFC6FF3D), AcentoOscuro = Color(0xFF4F7300), AcentoClaro = Color(0xFFEFFFD0), SobreAcento = Color(0xFF0A0A0A),
            Acento2 = Color(0xFF5B5BFF), Acento2Claro = Color(0xFFE8E8FF),
            Peligro = Color(0xFFFF4D6D), PeligroClaro = Color(0xFFFFE3E8),
            Aviso = Color(0xFFFFB020), AvisoClaro = Color(0xFFFFF3D6),
            Fondo = Color(0xFFF2F2EE), Tinta = Color(0xFF0B0B0B), TintaSuave = Color(0xFF5E5E66), Linea = Color(0xFFE2E2DC),
            FondoOscuro = Color(0xFF000000), TarjetaOscura = Color(0xFF121214), LineaOscura = Color(0xFF26262B),
        ),
    ),
}

val LocalEstilo = staticCompositionLocalOf { Estilo.Pulso }

/** Colores de marca del estilo activo. */
val Vitalis: Paleta
    @Composable @ReadOnlyComposable
    get() = LocalEstilo.current.paleta
