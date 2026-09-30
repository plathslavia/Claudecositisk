<?php
// GET citas.php?afiliado=1
// Devuelve las citas del afiliado (maestro) con sus pasos de preparación (detalle).
require __DIR__ . '/conexion.php';

$afiliado = filter_input(INPUT_GET, 'afiliado', FILTER_VALIDATE_INT) ?: 1;

try {
    $db = conectar();

    $consulta = $db->prepare(
        'SELECT c.id_cita AS id, e.codigo AS especialidad, p.nombre AS profesional, p.registro,
                c.fecha, c.hora, c.duracion_min, c.modalidad, s.nombre AS sede, c.lugar, c.estado,
                c.motivo, c.autorizacion, c.cuota_moderadora
           FROM citas c
           JOIN profesionales p  ON p.id_profesional  = c.id_profesional
           JOIN especialidades e ON e.id_especialidad = p.id_especialidad
           JOIN sedes s          ON s.id_sede         = c.id_sede
          WHERE c.id_afiliado = :afiliado
          ORDER BY c.fecha, c.hora'
    );
    $consulta->execute([':afiliado' => $afiliado]);
    $citas = $consulta->fetchAll();

    $pasos = $db->prepare('SELECT texto FROM preparaciones WHERE id_cita = :id ORDER BY orden');
    foreach ($citas as &$cita) {
        $cita['id'] = (int) $cita['id'];
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
