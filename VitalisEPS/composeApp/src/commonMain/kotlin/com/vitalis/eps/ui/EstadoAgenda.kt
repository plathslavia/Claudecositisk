package com.vitalis.eps.ui

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.vitalis.eps.data.Afiliado
import com.vitalis.eps.data.Agenda
import com.vitalis.eps.data.Catalogos
import com.vitalis.eps.data.Cita
import com.vitalis.eps.data.ClienteApi
import com.vitalis.eps.data.CuentasDemo
import com.vitalis.eps.data.ErrorServidor
import com.vitalis.eps.data.Especialidad
import com.vitalis.eps.data.Estado
import com.vitalis.eps.data.Modalidad
import com.vitalis.eps.data.NuevaCita
import com.vitalis.eps.data.Profesional
import com.vitalis.eps.data.Rol
import com.vitalis.eps.data.Sede
import com.vitalis.eps.data.Sesion
import com.vitalis.eps.data.fechaDesdeIso
import com.vitalis.eps.data.horaLegible
import com.vitalis.eps.data.urlServidorPorDefecto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

enum class Filtro(val etiqueta: String) {
    Proximas("Próximas"),
    PorConfirmar("Por confirmar"),
    Historial("Historial"),
    Todas("Todas"),
}

/** De dónde salen los datos que se ven en pantalla. */
enum class Origen { Conectando, Servidor, Ejemplo }

/**
 * Estado de la app: la sesión, las citas (del servidor XAMPP o, sin conexión, las de ejemplo),
 * los catálogos del panel de administración, la búsqueda y el filtro.
 * Vive fuera de las pantallas para que todas vean siempre los mismos datos.
 */
class EstadoAgenda {
    var busqueda by mutableStateOf("")
    var filtro by mutableStateOf(Filtro.Proximas)
    val lista = LazyListState()
    val listaAdmin = LazyListState()

    var urlServidor by mutableStateOf(urlServidorPorDefecto)
    var origen by mutableStateOf(Origen.Conectando)
        private set
    var mensaje by mutableStateOf<String?>(null)
        private set

    var sesion by mutableStateOf<Sesion?>(null)
        private set

    private var base by mutableStateOf(Agenda.citas)
    private val cambios = mutableStateMapOf<String, Estado>()
    var afiliados by mutableStateOf(Catalogos.afiliados)
        private set
    var profesionales by mutableStateOf(Catalogos.profesionales)
        private set
    var sedes by mutableStateOf(Catalogos.sedes)
        private set

    // Un solo cliente HTTP por dirección: crear uno por petición desperdicia hilos y conexiones.
    private var cliente: ClienteApi? = null
    private var urlCliente = ""
    private fun api(): ClienteApi {
        val actual = cliente
        if (actual != null && urlCliente == urlServidor) return actual
        actual?.close()
        return ClienteApi(urlServidor).also {
            it.token = sesion?.token
            cliente = it
            urlCliente = urlServidor
        }
    }

    private val enServidor get() = sesion?.sinConexion == false

    /** Todas las citas cargadas, con los cambios de estado aplicados. */
    private val todas: List<Cita> by derivedStateOf { base.map { c -> cambios[c.id]?.let { c.copy(estado = it) } ?: c } }

    /** Lo que puede ver la sesión actual: un afiliado, solo sus citas; el administrador, todas. */
    val citas: List<Cita> by derivedStateOf {
        val s = sesion
        when {
            s == null -> emptyList()
            s.rol == Rol.Admin -> todas
            else -> todas.filter { it.idAfiliado == s.idAfiliado }
        }
    }

    fun cita(id: String): Cita? = citas.firstOrNull { it.id == id }

    val proxima: Cita? by derivedStateOf { citas.filter { it.estado.esProxima }.minByOrNull { it.fecha.orden } }

    // ------------------------------------------------------------------------- servidor y sesión

    /** Comprueba si la API de XAMPP responde (para la píldora de estado del login). */
    suspend fun probarServidor() {
        origen = Origen.Conectando
        origen = try {
            api().probar()
            mensaje = null
            Origen.Servidor
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            mensaje = "No hubo respuesta de $urlServidor."
            Origen.Ejemplo
        }
    }

    /**
     * Inicia sesión en el servidor. Si el servidor no responde, prueba con las cuentas de prueba
     * locales (modo sin conexión). Devuelve un mensaje de error o null si todo salió bien.
     */
    suspend fun iniciarSesion(usuario: String, clave: String): String? {
        if (usuario.isBlank() || clave.isEmpty()) return "Escribe tu usuario y tu contraseña."
        return try {
            val nueva = api().iniciarSesion(usuario, clave)
            sesion = nueva
            origen = Origen.Servidor
            sincronizar()
            null
        } catch (e: CancellationException) {
            throw e
        } catch (e: ErrorServidor) {
            e.message
        } catch (e: Exception) {
            val local = CuentasDemo.validar(usuario, clave)
                ?: return "No hay conexión con el servidor. Sin conexión solo funcionan las cuentas de prueba."
            sesion = local
            origen = Origen.Ejemplo
            base = Agenda.citas
            cambios.clear()
            afiliados = Catalogos.afiliados
            profesionales = Catalogos.profesionales
            sedes = Catalogos.sedes
            mensaje = "Sin conexión con el servidor: estás en modo demostración con datos de ejemplo."
            null
        }
    }

    fun cerrarSesion(alcance: CoroutineScope) {
        val conServidor = enServidor
        val clienteActual = cliente
        sesion = null
        busqueda = ""
        filtro = Filtro.Proximas
        cambios.clear()
        base = Agenda.citas
        if (conServidor && clienteActual != null) {
            alcance.launch {
                try {
                    clienteActual.cerrarSesion()
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    // Si falla, el token vence solo a las 8 horas.
                }
            }
        }
    }

    /** Vuelve a leer las citas (y, si es administrador, los catálogos) desde MySQL. */
    suspend fun sincronizar() {
        if (!enServidor) return
        try {
            base = api().citas()
            cambios.clear()
            if (sesion?.rol == Rol.Admin) {
                val c = api().catalogos()
                afiliados = c.afiliados
                profesionales = c.profesionales
                sedes = c.sedes
            }
            origen = Origen.Servidor
            mensaje = null
        } catch (e: CancellationException) {
            throw e
        } catch (e: ErrorServidor) {
            mensaje = e.message
        } catch (e: Exception) {
            origen = Origen.Ejemplo
            mensaje = "Se perdió la conexión con el servidor."
        }
    }

    // ------------------------------------------------------------------------- acciones sobre citas

    /** Cambia el estado en pantalla de inmediato y, si hay servidor, lo guarda en MySQL. */
    fun cambiarEstado(id: String, estado: Estado, alcance: CoroutineScope) {
        val anterior = cita(id)?.estado ?: return
        cambios[id] = estado
        if (!enServidor) return
        alcance.launch {
            try {
                api().cambiarEstado(id, estado)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                cambios[id] = anterior
                mensaje = e.message ?: "No se pudo guardar el cambio en el servidor."
            }
        }
    }

    /** Crea una cita (solo administrador). Devuelve un mensaje de error o null. */
    suspend fun crearCita(nueva: NuevaCita): String? {
        if (!enServidor) {
            val profesional = profesionales.firstOrNull { it.id == nueva.idProfesional } ?: return "Elige un profesional."
            val afiliado = afiliados.firstOrNull { it.id == nueva.idAfiliado } ?: return "Elige un paciente."
            val sede = sedes.firstOrNull { it.id == nueva.idSede } ?: return "Elige una sede."
            val id = ((base.maxOfOrNull { it.id.toIntOrNull() ?: 0 } ?: 0) + 1).toString()
            base = base + Cita(
                id = id,
                especialidad = profesional.especialidad,
                profesional = profesional.nombre,
                registro = profesional.registro,
                fecha = fechaDesdeIso(nueva.fecha),
                hora = horaLegible(nueva.hora),
                duracionMin = nueva.duracionMin,
                modalidad = nueva.modalidad,
                sede = sede.nombre,
                lugar = if (nueva.modalidad == Modalidad.Telemedicina) "El enlace se activa 10 minutos antes" else sede.direccion.ifBlank { sede.nombre },
                estado = Estado.PorConfirmar,
                motivo = nueva.motivo,
                preparacion = nueva.preparacion,
                autorizacion = "AUT-2026-${10000 + id.toInt() * 37 % 89999}",
                cuotaModeradora = nueva.cuotaModeradora,
                idAfiliado = afiliado.id,
                paciente = afiliado.nombre,
            )
            return null
        }
        return ejecutar { api().crearCita(nueva) }
    }

    /** Agrega un profesional (solo administrador). Devuelve un mensaje de error o null. */
    suspend fun crearProfesional(nombre: String, registro: String, especialidad: Especialidad): String? {
        if (nombre.isBlank() || registro.isBlank()) return "Escribe el nombre y el registro profesional."
        if (!enServidor) {
            val id = (profesionales.maxOfOrNull { it.id } ?: 0) + 1
            profesionales = profesionales + Profesional(id, nombre.trim(), registro.trim(), especialidad)
            return null
        }
        return ejecutar { api().crearProfesional(nombre.trim(), registro.trim(), especialidad) }
    }

    /** Elimina una cita (solo administrador). */
    suspend fun eliminarCita(id: String): String? {
        if (!enServidor) {
            base = base.filterNot { it.id == id }
            return null
        }
        return ejecutar { api().eliminarCita(id) }
    }

    /** Ejecuta una acción en el servidor y recarga los datos; devuelve el mensaje de error, si hubo. */
    private suspend fun ejecutar(accion: suspend () -> Any): String? = try {
        accion()
        sincronizar()
        null
    } catch (e: CancellationException) {
        throw e
    } catch (e: ErrorServidor) {
        e.message
    } catch (e: Exception) {
        "No hay conexión con el servidor."
    }

    // ------------------------------------------------------------------------- búsqueda y filtros

    val resultados: List<Cita> by derivedStateOf { filtrar(citas) }

    private fun filtrar(lista: List<Cita>): List<Cita> {
        val texto = busqueda.trim()
        return lista
            .filter { c ->
                when (filtro) {
                    Filtro.Proximas -> c.estado.esProxima
                    Filtro.PorConfirmar -> c.estado == Estado.PorConfirmar
                    Filtro.Historial -> !c.estado.esProxima
                    Filtro.Todas -> true
                }
            }
            .filter { c ->
                texto.isEmpty() || listOf(c.especialidad.nombre, c.profesional, c.sede, c.modalidad.etiqueta, c.paciente)
                    .any { it.contains(texto, ignoreCase = true) }
            }
            .sortedWith(compareBy<Cita> { !it.estado.esProxima }.thenBy { if (it.estado.esProxima) it.fecha.orden else -it.fecha.orden })
    }

    fun afiliado(id: Int): Afiliado? = afiliados.firstOrNull { it.id == id }
    fun sede(id: Int): Sede? = sedes.firstOrNull { it.id == id }
}
