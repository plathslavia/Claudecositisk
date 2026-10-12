<?php
// POST logout.php   (cabecera X-Token) · Cierra la sesión borrando el token.
require __DIR__ . '/conexion.php';
soloPost();

try {
    $db = conectar();
    requerirSesion($db);
    $db->prepare('DELETE FROM sesiones WHERE token = :token')->execute([':token' => $_SERVER['HTTP_X_TOKEN']]);
    responder(200, ['ok' => true]);
} catch (Throwable $e) {
    responder(500, ['ok' => false, 'error' => 'Error de base de datos: ' . $e->getMessage()]);
}
