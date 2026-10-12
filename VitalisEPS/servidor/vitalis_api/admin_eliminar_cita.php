<?php
// POST admin_eliminar_cita.php   (cabecera X-Token, solo administrador)   cuerpo JSON: {"id": 12}
// Borra la cita; sus pasos de preparación se borran solos (ON DELETE CASCADE).
require __DIR__ . '/conexion.php';
soloPost();

$datos = leerJson();
$id = isset($datos['id']) ? filter_var($datos['id'], FILTER_VALIDATE_INT) : false;
if ($id === false) {
    responder(400, ['ok' => false, 'error' => 'Datos inválidos']);
}

try {
    $db = conectar();
    requerirSesion($db, 'admin');
    $borrar = $db->prepare('DELETE FROM citas WHERE id_cita = :id');
    $borrar->execute([':id' => $id]);
    if ($borrar->rowCount() === 0) {
        responder(404, ['ok' => false, 'error' => 'La cita no existe']);
    }
    responder(200, ['ok' => true]);
} catch (Throwable $e) {
    responder(500, ['ok' => false, 'error' => 'Error de base de datos: ' . $e->getMessage()]);
}
