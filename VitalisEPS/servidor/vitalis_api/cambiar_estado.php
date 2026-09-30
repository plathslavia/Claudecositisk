<?php
// POST cambiar_estado.php   cuerpo JSON: {"id": 4, "estado": "Confirmada"}
// Cambia el estado de una cita (confirmar o cancelar desde la app).
require __DIR__ . '/conexion.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    responder(405, ['ok' => false, 'error' => 'Usa POST']);
}

$datos = json_decode(file_get_contents('php://input'), true);
$id = isset($datos['id']) ? filter_var($datos['id'], FILTER_VALIDATE_INT) : false;
$estado = $datos['estado'] ?? '';
$permitidos = ['Confirmada', 'PorConfirmar', 'Atendida', 'Cancelada'];

if ($id === false || !in_array($estado, $permitidos, true)) {
    responder(400, ['ok' => false, 'error' => 'Datos inválidos']);
}

try {
    $db = conectar();
    $cambio = $db->prepare('UPDATE citas SET estado = :estado WHERE id_cita = :id');
    $cambio->execute([':estado' => $estado, ':id' => $id]);

    $existe = $db->prepare('SELECT COUNT(*) FROM citas WHERE id_cita = :id');
    $existe->execute([':id' => $id]);
    if ((int) $existe->fetchColumn() === 0) {
        responder(404, ['ok' => false, 'error' => 'La cita no existe']);
    }

    responder(200, ['ok' => true]);
} catch (Throwable $e) {
    responder(500, ['ok' => false, 'error' => 'Error de base de datos: ' . $e->getMessage()]);
}
