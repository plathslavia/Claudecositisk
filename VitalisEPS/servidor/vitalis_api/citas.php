<?php
// GET citas.php   (cabecera X-Token)
// Afiliado: devuelve solo sus citas. Administrador: devuelve las citas de todos los afiliados.
// Cada cita (maestro) trae sus pasos de preparación (detalle).
require __DIR__ . '/conexion.php';

try {
    $db = conectar();
    $usuario = requerirSesion($db);

    $sql = 'SELECT c.id_cita AS id, c.id_afiliado, a.nombre AS paciente, e.codigo AS especialidad,
                   p.nombre AS profesional, p.registro, c.fecha, c.hora, c.duracion_min, c.modalidad,
                   s.nombre AS sede, c.lugar, c.estado, c.motivo, c.autorizacion, c.cuota_moderadora
              FROM citas c
              JOIN afiliados a      ON a.id_afiliado     = c.id_afiliado
              JOIN profesionales p  ON p.id_profesional  = c.id_profesional
              JOIN especialidades e ON e.id_especialidad = p.id_especialidad
              JOIN sedes s          ON s.id_sede         = c.id_sede';
    $parametros = [];
    if ($usuario['rol'] !== 'admin') {
        $sql .= ' WHERE c.id_afiliado = :afiliado';
        $parametros[':afiliado'] = $usuario['id_afiliado'];
    }
    $sql .= ' ORDER BY c.fecha, c.hora';

    $consulta = $db->prepare($sql);
    $consulta->execute($parametros);
    $citas = $consulta->fetchAll();

    $pasos = $db->prepare('SELECT texto FROM preparaciones WHERE id_cita = :id ORDER BY orden');
    foreach ($citas as &$cita) {
        $cita['id'] = (int) $cita['id'];
        $cita['id_afiliado'] = (int) $cita['id_afiliado'];
        $cita['duracion_min'] = (int) $cita['duracion_min'];
        $cita['cuota_moderadora'] = (int) $cita['cuota_moderadora'];
        $pasos->execute([':id' => $cita['id']]);
        $cita['preparacion'] = $pasos->fetchAll(PDO::FETCH_COLUMN);
    }
    unset($cita);

    responder(200, ['ok' => true, 'citas' => $citas]);
} catch (Throwable $e) {
    responder(500, ['ok' => false, 'error' => 'Error de base de datos: ' . $e->getMessage()]);
}
