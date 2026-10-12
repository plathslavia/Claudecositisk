<?php
// POST admin_crear_profesional.php   (cabecera X-Token, solo administrador)
// Cuerpo JSON: {"nombre": "Dra. Ana Ruiz", "registro": "RM 12.345", "especialidad": "Cardiologia"}
require __DIR__ . '/conexion.php';
soloPost();

$datos = leerJson();
$nombre = trim((string) ($datos['nombre'] ?? ''));
$registro = trim((string) ($datos['registro'] ?? ''));
$especialidad = (string) ($datos['especialidad'] ?? '');

if ($nombre === '' || mb_strlen($nombre) > 100 || $registro === '' || mb_strlen($registro) > 30) {
    responder(400, ['ok' => false, 'error' => 'Escribe el nombre (máx. 100) y el registro profesional (máx. 30)']);
}

try {
    $db = conectar();
    requerirSesion($db, 'admin');

    $buscar = $db->prepare('SELECT id_especialidad FROM especialidades WHERE codigo = :codigo');
    $buscar->execute([':codigo' => $especialidad]);
    $idEspecialidad = $buscar->fetchColumn();
    if ($idEspecialidad === false) {
        responder(400, ['ok' => false, 'error' => 'La especialidad no existe']);
    }

    $nuevo = $db->prepare('INSERT INTO profesionales (nombre, registro, id_especialidad) VALUES (:nombre, :registro, :especialidad)');
    $nuevo->execute([':nombre' => $nombre, ':registro' => $registro, ':especialidad' => $idEspecialidad]);
    responder(200, ['ok' => true, 'id' => (int) $db->lastInsertId()]);
} catch (Throwable $e) {
    responder(500, ['ok' => false, 'error' => 'Error de base de datos: ' . $e->getMessage()]);
}
