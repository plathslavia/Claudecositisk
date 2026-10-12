<?php
// GET admin_catalogos.php   (cabecera X-Token, solo administrador)
// Listas para los formularios del panel: afiliados, profesionales, sedes y especialidades.
require __DIR__ . '/conexion.php';

try {
    $db = conectar();
    requerirSesion($db, 'admin');

    $afiliados = $db->query('SELECT id_afiliado AS id, nombre, documento FROM afiliados ORDER BY nombre')->fetchAll();
    $profesionales = $db->query(
        'SELECT p.id_profesional AS id, p.nombre, p.registro, e.codigo AS especialidad
           FROM profesionales p JOIN especialidades e ON e.id_especialidad = p.id_especialidad
          ORDER BY e.nombre, p.nombre'
    )->fetchAll();
    $sedes = $db->query('SELECT id_sede AS id, nombre, direccion FROM sedes ORDER BY id_sede')->fetchAll();
    $especialidades = $db->query('SELECT id_especialidad AS id, codigo, nombre FROM especialidades ORDER BY nombre')->fetchAll();

    foreach ([&$afiliados, &$profesionales, &$sedes, &$especialidades] as &$lista) {
        foreach ($lista as &$fila) {
            $fila['id'] = (int) $fila['id'];
        }
        unset($fila);
    }
    unset($lista);

    responder(200, [
        'ok' => true,
        'afiliados' => $afiliados,
        'profesionales' => $profesionales,
        'sedes' => $sedes,
        'especialidades' => $especialidades,
    ]);
} catch (Throwable $e) {
    responder(500, ['ok' => false, 'error' => 'Error de base de datos: ' . $e->getMessage()]);
}
