<?php
// POST login.php   cuerpo JSON: {"usuario": "1020456789", "clave": "camilo123"}
// Verifica la contraseña (bcrypt) y abre una sesión de 8 horas. Devuelve el token y el rol.
require __DIR__ . '/conexion.php';
soloPost();

$datos = leerJson();
$usuario = trim((string) ($datos['usuario'] ?? ''));
$clave = (string) ($datos['clave'] ?? '');
if ($usuario === '' || $clave === '' || strlen($usuario) > 40 || strlen($clave) > 100) {
    responder(400, ['ok' => false, 'error' => 'Escribe el usuario y la contraseña']);
}

try {
    $db = conectar();
    $consulta = $db->prepare('SELECT id_usuario, clave_hash, rol, nombre, id_afiliado FROM usuarios WHERE usuario = :usuario');
    $consulta->execute([':usuario' => $usuario]);
    $cuenta = $consulta->fetch();

    // Mismo mensaje si el usuario no existe o si la clave falla: no se revela cuál de los dos está mal.
    if (!$cuenta || !password_verify($clave, $cuenta['clave_hash'])) {
        responder(401, ['ok' => false, 'error' => 'Usuario o contraseña incorrectos']);
    }

    $db->exec('DELETE FROM sesiones WHERE expira < NOW()');
    $token = bin2hex(random_bytes(32));
    $sesion = $db->prepare('INSERT INTO sesiones (token, id_usuario, expira) VALUES (:token, :id, DATE_ADD(NOW(), INTERVAL 8 HOUR))');
    $sesion->execute([':token' => $token, ':id' => $cuenta['id_usuario']]);

    responder(200, [
        'ok' => true,
        'token' => $token,
        'rol' => $cuenta['rol'],
        'nombre' => $cuenta['nombre'],
        'id_afiliado' => $cuenta['id_afiliado'] === null ? null : (int) $cuenta['id_afiliado'],
    ]);
} catch (Throwable $e) {
    responder(500, ['ok' => false, 'error' => 'Error de base de datos: ' . $e->getMessage()]);
}
