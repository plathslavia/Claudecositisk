<?php
// POST admin_crear_cita.php   (cabecera X-Token, solo administrador)
// Cuerpo JSON: {"id_afiliado": 1, "id_profesional": 4, "id_sede": 3, "fecha": "2026-10-20", "hora": "09:30",
//               "duracion_min": 30, "modalidad": "Presencial", "motivo": "...", "cuota_moderadora": 5200,
//               "preparacion": ["Trae tu documento", "..."]}
// Crea la cita (maestro) y sus pasos de preparación (detalle) en una sola transacción.
require __DIR__ . '/conexion.php';
soloPost();

$datos = leerJson();
$entero = fn ($clave) => isset($datos[$clave]) ? filter_var($datos[$clave], FILTER_VALIDATE_INT) : false;

$idAfiliado = $entero('id_afiliado');
$idProfesional = $entero('id_profesional');
$idSede = $entero('id_sede');
$duracion = $entero('duracion_min');
$cuota = $entero('cuota_moderadora');
$fecha = (string) ($datos['fecha'] ?? '');
$hora = (string) ($datos['hora'] ?? '');
$modalidad = (string) ($datos['modalidad'] ?? '');
$motivo = trim((string) ($datos['motivo'] ?? ''));
$preparacion = $datos['preparacion'] ?? [];

$fechaValida = DateTime::createFromFormat('Y-m-d', $fecha);
$errores = [];
if ($idAfiliado === false || $idProfesional === false || $idSede === false) $errores[] = 'Elige paciente, profesional y sede';
if (!$fechaValida || $fechaValida->format('Y-m-d') !== $fecha) $errores[] = 'La fecha debe tener el formato AAAA-MM-DD';
if (!preg_match('/^([01]\d|2[0-3]):[0-5]\d$/', $hora)) $errores[] = 'La hora debe tener el formato HH:MM (24 horas)';
if ($duracion === false || $duracion < 5 || $duracion > 240) $errores[] = 'La duración debe estar entre 5 y 240 minutos';
if ($cuota === false || $cuota < 0 || $cuota > 1000000) $errores[] = 'La cuota moderadora no es válida';
if (!in_array($modalidad, ['Presencial', 'Telemedicina'], true)) $errores[] = 'Modalidad inválida';
if ($motivo === '' || mb_strlen($motivo) > 255) $errores[] = 'Escribe el motivo (máximo 255 caracteres)';
if (!is_array($preparacion) || count($preparacion) > 10) $errores[] = 'La preparación admite máximo 10 pasos';
if ($errores) {
    responder(400, ['ok' => false, 'error' => implode('. ', $errores)]);
}
$pasos = array_values(array_filter(array_map(fn ($p) => mb_substr(trim((string) $p), 0, 160), $preparacion), fn ($p) => $p !== ''));

try {
    $db = conectar();
    requerirSesion($db, 'admin');

    $sede = $db->prepare('SELECT nombre, direccion FROM sedes WHERE id_sede = :id');
    $sede->execute([':id' => $idSede]);
    $datosSede = $sede->fetch();
    if (!$datosSede) {
        responder(400, ['ok' => false, 'error' => 'La sede no existe']);
    }
    $lugar = $modalidad === 'Telemedicina' ? 'El enlace se activa 10 minutos antes' : ($datosSede['direccion'] ?: $datosSede['nombre']);
    $autorizacion = 'AUT-' . date('Y') . '-' . random_int(10000, 99999);

    $db->beginTransaction();
    $nueva = $db->prepare(
        'INSERT INTO citas (id_afiliado, id_profesional, id_sede, fecha, hora, duracion_min, modalidad, lugar, estado,
                            motivo, autorizacion, cuota_moderadora)
         VALUES (:afiliado, :profesional, :sede, :fecha, :hora, :duracion, :modalidad, :lugar, \'PorConfirmar\',
                 :motivo, :autorizacion, :cuota)'
    );
    $nueva->execute([
        ':afiliado' => $idAfiliado, ':profesional' => $idProfesional, ':sede' => $idSede,
        ':fecha' => $fecha, ':hora' => $hora . ':00', ':duracion' => $duracion, ':modalidad' => $modalidad,
        ':lugar' => $lugar, ':motivo' => $motivo, ':autorizacion' => $autorizacion, ':cuota' => $cuota,
    ]);
    $idCita = (int) $db->lastInsertId();

    $paso = $db->prepare('INSERT INTO preparaciones (id_cita, orden, texto) VALUES (:cita, :orden, :texto)');
    foreach ($pasos as $i => $texto) {
        $paso->execute([':cita' => $idCita, ':orden' => $i + 1, ':texto' => $texto]);
    }
    $db->commit();

    responder(200, ['ok' => true, 'id' => $idCita, 'autorizacion' => $autorizacion]);
} catch (PDOException $e) {
    if (isset($db) && $db->inTransaction()) $db->rollBack();
    // 23000 = violación de llave foránea: el paciente o el profesional no existen.
    $mensaje = $e->getCode() === '23000' ? 'El paciente o el profesional no existe' : 'Error de base de datos: ' . $e->getMessage();
    responder($e->getCode() === '23000' ? 400 : 500, ['ok' => false, 'error' => $mensaje]);
} catch (Throwable $e) {
    responder(500, ['ok' => false, 'error' => 'Error del servidor: ' . $e->getMessage()]);
}
