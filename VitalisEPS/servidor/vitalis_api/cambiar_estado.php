<?php
// POST cambiar_estado.php   (cabecera X-Token)   cuerpo JSON: {"id": 4, "estado": "Confirmada"}
// Afiliado: solo puede confirmar o cancelar sus propias citas.
// Administrador: puede poner cualquier estado en cualquier cita (por ejemplo, marcarla como atendida).
require __DIR__ . '/conexion.php';
soloPost();

$datos = leerJson();
$id = isset($datos['id']) ? filter_var($datos['id'], FILTER_VALIDATE_INT) : false;
$estado = $datos['estado'] ?? '';

try {
    $db = conectar();
    $usuario = requerirSesion($db);
    $esAdmin = $usuario['rol'] === 'admin';

    $permitidos = $esAdmin ? ['Confirmada', 'PorConfirmar', 'Atendida', 'Cancelada'] : ['Confirmada', 'Cancelada'];
    if ($id === false || !in_array($estado, $permitidos, true)) {
        responder(400, ['ok' => false, 'error' => 'Datos inválidos']);
    }

    $dueno = $db->prepare('SELECT id_afiliado FROM citas WHERE id_cita = :id');
    $dueno->execute([':id' => $id]);
    $idAfiliado = $dueno->fetchColumn();
    // Un afiliado recibe "no existe" también para citas ajenas: así no puede averiguar qué ids existen.
    if ($idAfiliado === false || (!$esAdmin && (int) $idAfiliado !== (int) $usuario['id_afiliado'])) {
        responder(404, ['ok' => false, 'error' => 'La cita no existe']);
    }

    $cambio = $db->prepare('UPDATE citas SET estado = :estado WHERE id_cita = :id');
    $cambio->execute([':estado' => $estado, ':id' => $id]);
    responder(200, ['ok' => true]);
} catch (Throwable $e) {
    responder(500, ['ok' => false, 'error' => 'Error de base de datos: ' . $e->getMessage()]);
}
