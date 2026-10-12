package com.vitalis.eps.data

import androidx.compose.ui.graphics.Color

/** Información institucional que se muestra en la pantalla de entrada (EPS ficticia). */
object Empresa {
    const val NOMBRE = "Vitalis EPS"
    const val ESLOGAN = "Agenda, confirma y reprograma tus citas médicas desde el celular, sin filas y sin llamadas eternas."
    const val QUIENES_SOMOS =
        "Somos una Entidad Promotora de Salud del régimen contributivo, fundada en Bogotá en 2012. " +
            "Coordinamos la atención de nuestros afiliados con una red de clínicas, laboratorios y especialistas, " +
            "y llevamos al celular lo que antes exigía una fila: pedir citas, ver autorizaciones y hablar con el médico por videollamada."
    const val MISION =
        "Garantizar a cada afiliado una atención en salud oportuna, humana y sin trámites innecesarios, " +
            "con una red de prestadores cercana y canales digitales disponibles todo el día."
    const val VISION =
        "Ser en 2030 la EPS mejor calificada del país en oportunidad de citas y satisfacción de los afiliados."

    val cifras = listOf(
        Cifra("1,2 M", "Afiliados"),
        Cifra("340", "IPS en la red"),
        Cifra("28", "Ciudades"),
    )

    val servicios = listOf(
        Servicio(TipoServicio.Consulta, "Medicina general y especialistas", "Más de 30 especialidades en toda la red."),
        Servicio(TipoServicio.Telemedicina, "Telemedicina 24/7", "Consulta por videollamada desde la casa."),
        Servicio(TipoServicio.Laboratorio, "Laboratorio clínico", "Resultados en la app en menos de 48 horas."),
        Servicio(TipoServicio.Prevencion, "Promoción y prevención", "Vacunación, control prenatal y chequeos."),
    )

    val canales = listOf(
        DatoContacto(TipoContacto.Linea, "Línea 24/7: 601 555 0188"),
        DatoContacto(TipoContacto.WhatsApp, "WhatsApp: +57 300 555 0101"),
        DatoContacto(TipoContacto.Correo, "atencion@vitalis-eps.co"),
        DatoContacto(TipoContacto.Sede, "Sede principal: Cra. 15 # 93-60, Bogotá"),
    )

    const val NIT = "NIT 900.555.321-7 · Entidad ficticia con fines académicos"

    const val AFILIADO = "Camilo Casallas"
    const val ID_AFILIADO = 1
    val equipo = listOf("Andrés García", "Camilo Casallas", "Cristóbal Moncada")
}

data class Cifra(val valor: String, val etiqueta: String)
enum class TipoServicio { Consulta, Telemedicina, Laboratorio, Prevencion }
data class Servicio(val tipo: TipoServicio, val titulo: String, val descripcion: String)
data class DatoContacto(val tipo: TipoContacto, val valor: String)
enum class TipoContacto { Linea, WhatsApp, Correo, Sede }

enum class Estado(val etiqueta: String) {
    Confirmada("Confirmada"),
    PorConfirmar("Por confirmar"),
    Atendida("Atendida"),
    Cancelada("Cancelada");

    val esProxima get() = this == Confirmada || this == PorConfirmar
}

enum class Modalidad(val etiqueta: String) { Presencial("Presencial"), Telemedicina("Telemedicina") }

enum class Especialidad(val nombre: String, val color: Color) {
    MedicinaGeneral("Medicina general", Color(0xFF12B89A)),
    Odontologia("Odontología", Color(0xFF3B82F6)),
    Psicologia("Psicología", Color(0xFF8B5CF6)),
    Cardiologia("Cardiología", Color(0xFFEF4461)),
    Laboratorio("Laboratorio clínico", Color(0xFFF59E0B)),
    Oftalmologia("Oftalmología", Color(0xFF0EA5E9)),
    Dermatologia("Dermatología", Color(0xFFEC4899)),
    Pediatria("Pediatría", Color(0xFF22C55E)),
    Fisioterapia("Fisioterapia", Color(0xFF6366F1)),
}

/** Fecha simple, sin depender de librerías de tiempo. */
data class Fecha(val diaSemana: String, val dia: Int, val mes: Int, val anio: Int) {
    val mesCorto get() = MESES[mes - 1].take(3)
    val mesLargo get() = MESES[mes - 1]
    val corta get() = "$diaSemana $dia $mesCorto"
    val larga get() = "$diaSemana, $dia de $mesLargo de $anio"
    val orden get() = anio * 10000 + mes * 100 + dia

    companion object {
        val MESES = listOf("enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre")
    }
}

/** Elemento de la lista: la vista Maestro muestra las citas y la vista Detalle una sola. */
data class Cita(
    val id: String,
    val especialidad: Especialidad,
    val profesional: String,
    val registro: String,
    val fecha: Fecha,
    val hora: String,
    val duracionMin: Int,
    val modalidad: Modalidad,
    val sede: String,
    val lugar: String,
    val estado: Estado,
    val motivo: String,
    val preparacion: List<String>,
    val autorizacion: String,
    val cuotaModeradora: Int,
    val idAfiliado: Int = Empresa.ID_AFILIADO,
    val paciente: String = Empresa.AFILIADO,
)

object Agenda {
    val citas = listOf(
        Cita(
            id = "1", especialidad = Especialidad.MedicinaGeneral,
            profesional = "Dra. Laura Méndez", registro = "RM 52.184",
            fecha = Fecha("Jue", 1, 10, 2026), hora = "7:30 a. m.", duracionMin = 20,
            modalidad = Modalidad.Presencial, sede = "Sede Chapinero", lugar = "Consultorio 204 · Cl. 63 # 13-22",
            estado = Estado.Confirmada,
            motivo = "Control de rutina y revisión de los exámenes de sangre del mes pasado.",
            preparacion = listOf("Llega 15 minutos antes para el registro", "Trae tu documento de identidad", "Lleva los resultados de laboratorio impresos o en la app"),
            autorizacion = "AUT-2026-58213", cuotaModeradora = 5200,
        ),
        Cita(
            id = "2", especialidad = Especialidad.Odontologia,
            profesional = "Dr. Santiago Rojas", registro = "RM 77.410",
            fecha = Fecha("Vie", 2, 10, 2026), hora = "10:00 a. m.", duracionMin = 40,
            modalidad = Modalidad.Presencial, sede = "Sede Usaquén", lugar = "Consultorio 12 · Cra. 7 # 119-14",
            estado = Estado.PorConfirmar,
            motivo = "Limpieza dental semestral y valoración de sensibilidad en muelas.",
            preparacion = listOf("Cepíllate antes de la cita", "Si tomas anticoagulantes, avísale al odontólogo"),
            autorizacion = "AUT-2026-58877", cuotaModeradora = 5200,
        ),
        Cita(
            id = "3", especialidad = Especialidad.Psicologia,
            profesional = "Dra. Mariana Ortiz", registro = "TP 118.502",
            fecha = Fecha("Lun", 5, 10, 2026), hora = "4:30 p. m.", duracionMin = 50,
            modalidad = Modalidad.Telemedicina, sede = "Videollamada", lugar = "El enlace se activa 10 minutos antes",
            estado = Estado.Confirmada,
            motivo = "Sesión de seguimiento del manejo del estrés académico.",
            preparacion = listOf("Busca un lugar tranquilo y con buena señal", "Usa audífonos para más privacidad", "Ten a mano tus notas de la semana"),
            autorizacion = "AUT-2026-59002", cuotaModeradora = 5200,
        ),
        Cita(
            id = "4", especialidad = Especialidad.Cardiologia,
            profesional = "Dr. Felipe Castaño", registro = "RM 40.967",
            fecha = Fecha("Mié", 7, 10, 2026), hora = "2:15 p. m.", duracionMin = 30,
            modalidad = Modalidad.Presencial, sede = "Sede Salitre", lugar = "Torre B, piso 5 · Av. Cl. 26 # 68C-61",
            estado = Estado.PorConfirmar,
            motivo = "Valoración por palpitaciones ocasionales y lectura de electrocardiograma.",
            preparacion = listOf("No tomes café ni bebidas energizantes 12 horas antes", "Trae el electrocardiograma anterior", "Usa ropa cómoda"),
            autorizacion = "AUT-2026-59318", cuotaModeradora = 5200,
        ),
        Cita(
            id = "5", especialidad = Especialidad.Laboratorio,
            profesional = "Toma de muestras", registro = "Laboratorio Vitalis",
            fecha = Fecha("Sáb", 10, 10, 2026), hora = "6:30 a. m.", duracionMin = 15,
            modalidad = Modalidad.Presencial, sede = "Sede Chapinero", lugar = "Primer piso · Cl. 63 # 13-22",
            estado = Estado.Confirmada,
            motivo = "Cuadro hemático, perfil lipídico y glicemia.",
            preparacion = listOf("Ayuno de 8 a 12 horas (solo agua)", "Trae la orden médica", "No hagas ejercicio fuerte el día anterior"),
            autorizacion = "AUT-2026-59480", cuotaModeradora = 0,
        ),
        Cita(
            id = "6", especialidad = Especialidad.Oftalmologia,
            profesional = "Dra. Paula Herrera", registro = "RM 63.221",
            fecha = Fecha("Mar", 13, 10, 2026), hora = "9:00 a. m.", duracionMin = 30,
            modalidad = Modalidad.Presencial, sede = "Sede Salitre", lugar = "Torre A, piso 3 · Av. Cl. 26 # 68C-61",
            estado = Estado.PorConfirmar,
            motivo = "Revisión de fórmula de lentes y control de visión borrosa de lejos.",
            preparacion = listOf("Trae tus gafas actuales", "Ve acompañado: pueden dilatarte las pupilas"),
            autorizacion = "AUT-2026-59711", cuotaModeradora = 5200,
        ),
        Cita(
            id = "7", especialidad = Especialidad.Dermatologia,
            profesional = "Dr. Julián Pardo", registro = "RM 58.340",
            fecha = Fecha("Mar", 22, 9, 2026), hora = "11:20 a. m.", duracionMin = 20,
            modalidad = Modalidad.Presencial, sede = "Sede Usaquén", lugar = "Consultorio 31 · Cra. 7 # 119-14",
            estado = Estado.Atendida,
            motivo = "Control de dermatitis en manos.",
            preparacion = listOf("Trae los medicamentos que estás usando"),
            autorizacion = "AUT-2026-57102", cuotaModeradora = 5200,
        ),
        Cita(
            id = "8", especialidad = Especialidad.Fisioterapia,
            profesional = "Ft. Daniel Suárez", registro = "TP 30.518",
            fecha = Fecha("Jue", 10, 9, 2026), hora = "5:00 p. m.", duracionMin = 45,
            modalidad = Modalidad.Presencial, sede = "Sede Chapinero", lugar = "Gimnasio terapéutico · Cl. 63 # 13-22",
            estado = Estado.Atendida,
            motivo = "Terapia física para dolor lumbar (sesión 4 de 10).",
            preparacion = listOf("Usa ropa deportiva"),
            autorizacion = "AUT-2026-56390", cuotaModeradora = 5200,
        ),
        Cita(
            id = "9", especialidad = Especialidad.Pediatria,
            profesional = "Dra. Catalina Vega", registro = "RM 49.875",
            fecha = Fecha("Lun", 7, 9, 2026), hora = "8:40 a. m.", duracionMin = 20,
            modalidad = Modalidad.Presencial, sede = "Sede Salitre", lugar = "Torre A, piso 2 · Av. Cl. 26 # 68C-61",
            estado = Estado.Cancelada,
            motivo = "Control de crecimiento y desarrollo.",
            preparacion = listOf("Trae el carné de vacunación"),
            autorizacion = "AUT-2026-55811", cuotaModeradora = 0,
        ),
        Cita(
            id = "10", especialidad = Especialidad.MedicinaGeneral,
            profesional = "Dra. Laura Méndez", registro = "RM 52.184",
            fecha = Fecha("Sáb", 3, 10, 2026), hora = "8:00 a. m.", duracionMin = 20,
            modalidad = Modalidad.Presencial, sede = "Sede Usaquén", lugar = "Consultorio 15 · Cra. 7 # 119-14",
            estado = Estado.Confirmada,
            motivo = "Revisión de presión arterial.",
            preparacion = listOf("No fumes ni tomes café 30 minutos antes"),
            autorizacion = "AUT-2026-60110", cuotaModeradora = 5200,
            idAfiliado = 2, paciente = "Andrés García",
        ),
        Cita(
            id = "11", especialidad = Especialidad.Cardiologia,
            profesional = "Dr. Felipe Castaño", registro = "RM 40.967",
            fecha = Fecha("Vie", 9, 10, 2026), hora = "3:00 p. m.", duracionMin = 30,
            modalidad = Modalidad.Presencial, sede = "Sede Salitre", lugar = "Torre B, piso 5 · Av. Cl. 26 # 68C-61",
            estado = Estado.PorConfirmar,
            motivo = "Control con electrocardiograma.",
            preparacion = listOf("Trae los exámenes anteriores"),
            autorizacion = "AUT-2026-60255", cuotaModeradora = 5200,
            idAfiliado = 2, paciente = "Andrés García",
        ),
        Cita(
            id = "12", especialidad = Especialidad.Psicologia,
            profesional = "Dra. Mariana Ortiz", registro = "TP 118.502",
            fecha = Fecha("Mar", 6, 10, 2026), hora = "6:00 p. m.", duracionMin = 50,
            modalidad = Modalidad.Telemedicina, sede = "Videollamada", lugar = "El enlace se activa 10 minutos antes",
            estado = Estado.PorConfirmar,
            motivo = "Primera consulta de orientación.",
            preparacion = listOf("Busca un lugar tranquilo y con buena señal"),
            autorizacion = "AUT-2026-60301", cuotaModeradora = 5200,
            idAfiliado = 3, paciente = "Cristóbal Moncada",
        ),
    )

    fun porId(id: String): Cita? = citas.firstOrNull { it.id == id }
}

/** Formatea pesos colombianos con punto de miles: 5200 -> "$ 5.200". */
fun pesos(valor: Int): String =
    if (valor == 0) "Sin costo" else "$ " + valor.toString().reversed().chunked(3).joinToString(".").reversed()

/** "Dra. Laura Méndez" -> "LM" */
fun iniciales(nombre: String): String =
    nombre.split(" ").filter { it.isNotEmpty() && !it.endsWith(".") }.take(2).joinToString("") { it.first().uppercase() }

// ---------------------------------------------------------------------------------------------
//  Sesión, roles y catálogos del panel de administración
// ---------------------------------------------------------------------------------------------

enum class Rol { Afiliado, Admin }

/** Sesión abierta. [token] es nulo en el modo sin conexión (cuentas de prueba locales). */
data class Sesion(val token: String?, val rol: Rol, val nombre: String, val idAfiliado: Int?) {
    val sinConexion get() = token == null
    val primerNombre get() = nombre.substringBefore(" ")
}

data class Afiliado(val id: Int, val nombre: String, val documento: String)
data class Profesional(val id: Int, val nombre: String, val registro: String, val especialidad: Especialidad)
data class Sede(val id: Int, val nombre: String, val direccion: String)

/** Datos que el administrador escribe en el formulario de nueva cita. */
data class NuevaCita(
    val idAfiliado: Int,
    val idProfesional: Int,
    val idSede: Int,
    /** AAAA-MM-DD */
    val fecha: String,
    /** HH:MM, 24 horas */
    val hora: String,
    val duracionMin: Int,
    val modalidad: Modalidad,
    val motivo: String,
    val cuotaModeradora: Int,
    val preparacion: List<String>,
)

/** Los mismos catálogos del script SQL, para el modo sin conexión. */
object Catalogos {
    val afiliados = listOf(
        Afiliado(1, "Camilo Casallas", "1020456789"),
        Afiliado(2, "Andrés García", "1018223344"),
        Afiliado(3, "Cristóbal Moncada", "1032667788"),
    )
    val profesionales = listOf(
        Profesional(1, "Dra. Laura Méndez", "RM 52.184", Especialidad.MedicinaGeneral),
        Profesional(2, "Dr. Santiago Rojas", "RM 77.410", Especialidad.Odontologia),
        Profesional(3, "Dra. Mariana Ortiz", "TP 118.502", Especialidad.Psicologia),
        Profesional(4, "Dr. Felipe Castaño", "RM 40.967", Especialidad.Cardiologia),
        Profesional(5, "Toma de muestras", "Laboratorio Vitalis", Especialidad.Laboratorio),
        Profesional(6, "Dra. Paula Herrera", "RM 63.221", Especialidad.Oftalmologia),
        Profesional(7, "Dr. Julián Pardo", "RM 58.340", Especialidad.Dermatologia),
        Profesional(8, "Ft. Daniel Suárez", "TP 30.518", Especialidad.Fisioterapia),
        Profesional(9, "Dra. Catalina Vega", "RM 49.875", Especialidad.Pediatria),
    )
    val sedes = listOf(
        Sede(1, "Sede Chapinero", "Cl. 63 # 13-22"),
        Sede(2, "Sede Usaquén", "Cra. 7 # 119-14"),
        Sede(3, "Sede Salitre", "Av. Cl. 26 # 68C-61"),
        Sede(4, "Videollamada", ""),
    )
}

/**
 * Cuentas de prueba para el modo sin conexión (cuando XAMPP no está encendido).
 * Son las mismas del script SQL; en el servidor las contraseñas se guardan cifradas con bcrypt.
 */
object CuentasDemo {
    class Cuenta(val usuario: String, val clave: String, val sesion: Sesion)

    val todas = listOf(
        Cuenta("1020456789", "camilo123", Sesion(null, Rol.Afiliado, "Camilo Casallas", 1)),
        Cuenta("1018223344", "andres123", Sesion(null, Rol.Afiliado, "Andrés García", 2)),
        Cuenta("1032667788", "cristobal123", Sesion(null, Rol.Afiliado, "Cristóbal Moncada", 3)),
        Cuenta("admin", "admin123", Sesion(null, Rol.Admin, "Administración Vitalis", null)),
    )

    fun validar(usuario: String, clave: String): Sesion? =
        todas.firstOrNull { it.usuario == usuario.trim() && it.clave == clave }?.sesion
}
