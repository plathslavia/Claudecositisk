<?php
// Conexión a la base de datos de XAMPP y utilidades comunes de la API.
// Con la instalación por defecto de XAMPP el usuario es "root" y la contraseña está vacía.

const DB_HOST = '127.0.0.1';
const DB_NOMBRE = 'vitalis_eps';
const DB_USUARIO = 'root';
const DB_CLAVE = '';

header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, X-Token');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(204);
    exit;
}

function conectar(): PDO
{
    return new PDO(
        'mysql:host=' . DB_HOST . ';dbname=' . DB_NOMBRE . ';charset=utf8mb4',
        DB_USUARIO,
        DB_CLAVE,
        [
            PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
        ]
    );
}

function responder(int $codigo, array $datos): void
{
    http_response_code($codigo);
    echo json_encode($datos, JSON_UNESCAPED_UNICODE);
    exit;
}

/** Lee el cuerpo JSON de la petición. */
function leerJson(): array
{
    $datos = json_decode(file_get_contents('php://input'), true);
    return is_array($datos) ? $datos : [];
}

/**
 * Valida el token de sesión (cabecera X-Token) y devuelve el usuario:
 * id_usuario, rol ('afiliado' o 'admin'), nombre e id_afiliado.
 * Si se pide un rol y el usuario no lo tiene, responde 403 y termina.
 */
function requerirSesion(PDO $db, ?string $rol = null): array
{
    $token = $_SERVER['HTTP_X_TOKEN'] ?? '';
    if (!preg_match('/^[a-f0-9]{64}$/', $token)) {
        responder(401, ['ok' => false, 'error' => 'Inicia sesión para continuar']);
    }
    $consulta = $db->prepare(
        'SELECT u.id_usuario, u.rol, u.nombre, u.id_afiliado
           FROM sesiones s JOIN usuarios u ON u.id_usuario = s.id_usuario
          WHERE s.token = :token AND s.expira > NOW()'
    );
    $consulta->execute([':token' => $token]);
    $usuario = $consulta->fetch();
    if (!$usuario) {
        responder(401, ['ok' => false, 'error' => 'La sesión venció. Vuelve a iniciar sesión']);
    }
    if ($rol !== null && $usuario['rol'] !== $rol) {
        responder(403, ['ok' => false, 'error' => 'No tienes permiso para esta acción']);
    }
    return $usuario;
}

function soloPost(): void
{
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        responder(405, ['ok' => false, 'error' => 'Usa POST']);
    }
}
