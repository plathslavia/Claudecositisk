package com.vitalis.eps.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Dirección de la API PHP que corre en XAMPP (carpeta htdocs/vitalis_api).
 * En el emulador de Android, 10.0.2.2 es el "localhost" del computador.
 */
expect val urlServidorPorDefecto: String

@Serializable
private data class CitaDto(
    val id: Int,
    @SerialName("id_afiliado") val idAfiliado: Int,
    val paciente: String,
    val especialidad: String,
    val profesional: String,
    val registro: String,
    val fecha: String,
    val hora: String,
    @SerialName("duracion_min") val duracionMin: Int,
    val modalidad: String,
    val sede: String,
    val lugar: String,
    val estado: String,
    val motivo: String,
    val autorizacion: String,
    @SerialName("cuota_moderadora") val cuotaModeradora: Int,
    val preparacion: List<String> = emptyList(),
)

@Serializable
private data class Respuesta(
    val ok: Boolean,
    val error: String? = null,
    val citas: List<CitaDto> = emptyList(),
    val token: String? = null,
    val rol: String? = null,
    val nombre: String? = null,
    @SerialName("id_afiliado") val idAfiliado: Int? = null,
    val id: Int? = null,
    val afiliados: List<AfiliadoDto> = emptyList(),
    val profesionales: List<ProfesionalDto> = emptyList(),
    val sedes: List<SedeDto> = emptyList(),
)

@Serializable private data class AfiliadoDto(val id: Int, val nombre: String, val documento: String)
@Serializable private data class ProfesionalDto(val id: Int, val nombre: String, val registro: String, val especialidad: String)
@Serializable private data class SedeDto(val id: Int, val nombre: String, val direccion: String)

@Serializable private data class Credenciales(val usuario: String, val clave: String)
@Serializable private data class CambioEstado(val id: Int, val estado: String)
@Serializable private data class SoloId(val id: Int)
@Serializable private data class NuevoProfesionalDto(val nombre: String, val registro: String, val especialidad: String)

@Serializable
private data class NuevaCitaDto(
    @SerialName("id_afiliado") val idAfiliado: Int,
    @SerialName("id_profesional") val idProfesional: Int,
    @SerialName("id_sede") val idSede: Int,
    val fecha: String,
    val hora: String,
    @SerialName("duracion_min") val duracionMin: Int,
    val modalidad: String,
    val motivo: String,
    @SerialName("cuota_moderadora") val cuotaModeradora: Int,
    val preparacion: List<String>,
)

/** El servidor respondió, pero con un error (clave incorrecta, sin permiso, datos inválidos…). */
class ErrorServidor(mensaje: String) : Exception(mensaje)

/** Catálogos para los formularios del panel de administración. */
data class DatosCatalogo(val afiliados: List<Afiliado>, val profesionales: List<Profesional>, val sedes: List<Sede>)

/**
 * Cliente de la API REST de la EPS. Envía el token de sesión en la cabecera X-Token.
 * Lanza [ErrorServidor] si el servidor rechaza la petición y otra excepción si no hay conexión.
 */
class ClienteApi(urlBase: String) : AutoCloseable {
    private val base = urlBase.trimEnd('/')
    var token: String? = null

    private val http = HttpClient {
        expectSuccess = false // Los errores (401, 403, 400) traen un mensaje en JSON que queremos mostrar.
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        install(HttpTimeout) {
            connectTimeoutMillis = 3_000
            requestTimeoutMillis = 6_000
        }
    }

    private suspend fun leer(respuesta: HttpResponse): Respuesta {
        val datos = try {
            respuesta.body<Respuesta>()
        } catch (e: Exception) {
            throw ErrorServidor("El servidor respondió algo inesperado (código ${respuesta.status.value}).")
        }
        if (!datos.ok) throw ErrorServidor(datos.error ?: "El servidor rechazó la petición")
        return datos
    }

    private suspend fun obtener(ruta: String) = leer(http.get("$base/$ruta") { token?.let { header("X-Token", it) } })

    private suspend inline fun <reified T> enviar(ruta: String, cuerpo: T) = leer(
        http.post("$base/$ruta") {
            token?.let { header("X-Token", it) }
            contentType(ContentType.Application.Json)
            setBody(cuerpo)
        },
    )

    /** Comprueba que la API y MySQL respondan. */
    suspend fun probar() {
        obtener("index.php")
    }

    suspend fun iniciarSesion(usuario: String, clave: String): Sesion {
        val r = enviar("login.php", Credenciales(usuario.trim(), clave))
        val sesion = Sesion(
            token = r.token ?: throw ErrorServidor("El servidor no devolvió la sesión"),
            rol = if (r.rol == "admin") Rol.Admin else Rol.Afiliado,
            nombre = r.nombre ?: usuario,
            idAfiliado = r.idAfiliado,
        )
        token = sesion.token
        return sesion
    }

    suspend fun cerrarSesion() {
        enviar("logout.php", SoloId(0))
        token = null
    }

    suspend fun citas(): List<Cita> = obtener("citas.php").citas.map { it.aCita() }

    suspend fun cambiarEstado(idCita: String, estado: Estado) {
        enviar("cambiar_estado.php", CambioEstado(idCita.toInt(), estado.name))
    }

    suspend fun catalogos(): DatosCatalogo {
        val r = obtener("admin_catalogos.php")
        return DatosCatalogo(
            afiliados = r.afiliados.map { Afiliado(it.id, it.nombre, it.documento) },
            profesionales = r.profesionales.map {
                Profesional(it.id, it.nombre, it.registro, Especialidad.entries.firstOrNull { e -> e.name == it.especialidad } ?: Especialidad.MedicinaGeneral)
            },
            sedes = r.sedes.map { Sede(it.id, it.nombre, it.direccion) },
        )
    }

    suspend fun crearCita(c: NuevaCita): Int = enviar(
        "admin_crear_cita.php",
        NuevaCitaDto(c.idAfiliado, c.idProfesional, c.idSede, c.fecha, c.hora, c.duracionMin, c.modalidad.name, c.motivo, c.cuotaModeradora, c.preparacion),
    ).id ?: 0

    suspend fun crearProfesional(nombre: String, registro: String, especialidad: Especialidad): Int =
        enviar("admin_crear_profesional.php", NuevoProfesionalDto(nombre, registro, especialidad.name)).id ?: 0

    suspend fun eliminarCita(idCita: String) {
        enviar("admin_eliminar_cita.php", SoloId(idCita.toInt()))
    }

    override fun close() = http.close()
}

private fun CitaDto.aCita() = Cita(
    id = id.toString(),
    especialidad = Especialidad.entries.firstOrNull { it.name == especialidad } ?: Especialidad.MedicinaGeneral,
    profesional = profesional,
    registro = registro,
    fecha = fechaDesdeIso(fecha),
    hora = horaLegible(hora),
    duracionMin = duracionMin,
    modalidad = Modalidad.entries.firstOrNull { it.name == modalidad } ?: Modalidad.Presencial,
    sede = sede,
    lugar = lugar,
    estado = Estado.entries.firstOrNull { it.name == estado } ?: Estado.PorConfirmar,
    motivo = motivo,
    preparacion = preparacion,
    autorizacion = autorizacion,
    cuotaModeradora = cuotaModeradora,
    idAfiliado = idAfiliado,
    paciente = paciente,
)

private val DIAS = listOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb")

/** "2026-10-01" -> Fecha("Jue", 1, 10, 2026). El día de la semana se calcula con el algoritmo de Sakamoto. */
internal fun fechaDesdeIso(iso: String): Fecha {
    val (a, m, d) = iso.take(10).split("-").map { it.toInt() }
    val t = intArrayOf(0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4)
    val y = if (m < 3) a - 1 else a
    val dia = (y + y / 4 - y / 100 + y / 400 + t[m - 1] + d) % 7
    return Fecha(DIAS[dia], d, m, a)
}

/** "14:15:00" -> "2:15 p. m." */
internal fun horaLegible(hora: String): String {
    val (h, min) = hora.split(":").map { it.toInt() }
    val h12 = if (h % 12 == 0) 12 else h % 12
    return "$h12:${min.toString().padStart(2, '0')} ${if (h < 12) "a. m." else "p. m."}"
}
